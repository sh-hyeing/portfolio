const MIN_FORM_AGE_MS = 4000;
const SUBMIT_COOLDOWN_MS = 45000;
const URL_PATTERN = /(https?:\/\/|www\.|\.com\b|\.net\b|\.org\b|\.kr\b)/gi;
const REPEATED_CHARACTER_PATTERN = /(.)\1{12,}/;
const BLOCKED_KEYWORDS = ["casino", "바카라", "토토", "loan", "viagra", "porn", "free money", "crypto bonus"];
const recentSubmissions = new Map();

export async function onRequestGet(context) {
  const result = await context.env.DB.prepare(
    "SELECT id, name, message, reg_date AS regDate FROM guestbook ORDER BY id DESC LIMIT 50"
  ).all();

  return json(result.results || []);
}

export async function onRequestPost(context) {
  const body = await readJson(context.request);
  const spamResult = checkSpam(context.request, body);
  if (!spamResult.allowed) {
    return json({ error: "방명록 등록이 제한되었습니다. 잠시 후 다시 시도해 주세요." }, 429);
  }

  const validationError = validateGuestbook(body);
  if (validationError) {
    return json({ error: validationError }, 400);
  }

  const passwordHash = await hashPassword(body.password);
  await context.env.DB.prepare(
    "INSERT INTO guestbook (name, password_hash, message, reg_date) VALUES (?, ?, ?, datetime('now'))"
  )
    .bind(body.name.trim(), passwordHash, body.message.trim())
    .run();

  return json({ ok: true }, 201);
}

export async function onRequestDelete(context) {
  const id = Number(context.params.id);
  if (!Number.isInteger(id) || id < 1) {
    return json({ error: "삭제할 방명록을 찾지 못했습니다." }, 400);
  }

  const body = await readJson(context.request);
  if (!hasText(body.password)) {
    return json({ error: "비밀번호를 입력해 주세요." }, 400);
  }

  const guest = await context.env.DB.prepare("SELECT id, password_hash FROM guestbook WHERE id = ?").bind(id).first();
  if (!guest || !(await verifyPassword(body.password, guest.password_hash))) {
    return json({ error: "비밀번호가 일치하지 않습니다." }, 403);
  }

  await context.env.DB.prepare("DELETE FROM guestbook WHERE id = ?").bind(id).run();
  return json({ ok: true });
}

function validateGuestbook(body) {
  if (!hasText(body.name) || !hasText(body.password) || !hasText(body.message)) {
    return "이름, 비밀번호, 메시지를 확인해 주세요.";
  }

  if (body.name.trim().length > 20) {
    return "이름은 20자 이하로 입력해 주세요.";
  }

  const messageLength = body.message.trim().length;
  if (messageLength < 5 || messageLength > 1000) {
    return "메시지는 5자 이상 1000자 이하로 입력해 주세요.";
  }

  return "";
}

function checkSpam(request, body) {
  if (hasText(body.website)) {
    return { allowed: false, reason: "hidden-field" };
  }

  const submittedAt = Number(body.submittedAt);
  if (!Number.isFinite(submittedAt) || Date.now() - submittedAt < MIN_FORM_AGE_MS) {
    return { allowed: false, reason: "too-fast" };
  }

  const content = `${body.name || ""} ${body.message || ""}`.toLowerCase().trim();
  const urlCount = [...content.matchAll(URL_PATTERN)].length;
  if (urlCount > 1 || BLOCKED_KEYWORDS.some((keyword) => content.includes(keyword)) || REPEATED_CHARACTER_PATTERN.test(content)) {
    return { allowed: false, reason: "spam-content" };
  }

  const clientKey = getClientKey(request);
  if (clientKey) {
    const lastSubmittedAt = recentSubmissions.get(clientKey);
    if (lastSubmittedAt && Date.now() - lastSubmittedAt < SUBMIT_COOLDOWN_MS) {
      return { allowed: false, reason: "rate-limit" };
    }
    recentSubmissions.set(clientKey, Date.now());
  }

  return { allowed: true, reason: "" };
}

function getClientKey(request) {
  return request.headers.get("CF-Connecting-IP") || request.headers.get("X-Forwarded-For")?.split(",")[0]?.trim() || "";
}

async function readJson(request) {
  try {
    return await request.json();
  } catch {
    return {};
  }
}

async function hashPassword(password) {
  const salt = crypto.getRandomValues(new Uint8Array(16));
  const iterations = 210000;
  const key = await derivePasswordKey(password, salt, iterations);
  return `pbkdf2_sha256$${iterations}$${toBase64(salt)}$${toBase64(key)}`;
}

async function verifyPassword(password, storedHash) {
  const [algorithm, iterationsValue, saltValue, hashValue] = String(storedHash || "").split("$");
  if (algorithm !== "pbkdf2_sha256") return false;

  const salt = fromBase64(saltValue);
  const expected = fromBase64(hashValue);
  const actual = await derivePasswordKey(password, salt, Number(iterationsValue));
  return timingSafeEqual(actual, expected);
}

async function derivePasswordKey(password, salt, iterations) {
  const encodedPassword = new TextEncoder().encode(password);
  const keyMaterial = await crypto.subtle.importKey("raw", encodedPassword, "PBKDF2", false, ["deriveBits"]);
  const bits = await crypto.subtle.deriveBits(
    { name: "PBKDF2", hash: "SHA-256", salt, iterations },
    keyMaterial,
    256
  );
  return new Uint8Array(bits);
}

function timingSafeEqual(a, b) {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i += 1) {
    diff |= a[i] ^ b[i];
  }
  return diff === 0;
}

function toBase64(bytes) {
  return btoa(String.fromCharCode(...bytes));
}

function fromBase64(value) {
  return Uint8Array.from(atob(value), (char) => char.charCodeAt(0));
}

function hasText(value) {
  return typeof value === "string" && value.trim().length > 0;
}

function json(payload, status = 200) {
  return Response.json(payload, {
    status,
    headers: {
      "Cache-Control": "no-store",
    },
  });
}
