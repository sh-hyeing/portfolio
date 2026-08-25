import { cp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const templatesDir = path.join(root, "src", "main", "resources", "templates");
const staticDir = path.join(root, "src", "main", "resources", "static");
const distDir = path.join(root, "dist");

const navHtml = `
<div class="main-menu">
 <a href="/#home" class="theme-home">
  <div class="folder-tab-top"></div>
  <div class="folder-tab-body"><span>Home</span></div>
 </a>
 <a href="/#about" class="theme-about">
  <div class="folder-tab-top"></div>
  <div class="folder-tab-body"><span>About</span></div>
 </a>
 <a href="/#work" class="theme-work">
  <div class="folder-tab-top"></div>
  <div class="folder-tab-body"><span>Project</span></div>
 </a>
 <a href="/#guest" class="theme-guest">
  <div class="folder-tab-top"></div>
  <div class="folder-tab-body"><span>Guest</span></div>
 </a>
</div>`;

const footerHtml = `
<footer class="site-footer">
 <div class="footer-inner">
  <div class="footer-meta">
   <div class="footer-links" aria-label="Contact links">
    <a href="https://github.com/sh-hyeing" target="_blank" rel="noopener" aria-label="GitHub">
     <span>GitHub</span>
    </a>
    <span class="footer-separator" aria-hidden="true">/</span>
    <a href="mailto:sh.hyeing@gmail.com">sh.hyeing@gmail.com</a>
   </div>
   <span class="footer-copy">© JEONG HYEIN</span>
  </div>
 </div>
</footer>`;

const projects = {
  "yt-script-project": {
    title: "Youtube Script Maker",
    subtitle: "유튜브 영상 자막을 불러와 영어·한국어 학습용 스크립트로 정리하고 PDF로 저장할 수 있는 웹 기반 학습 도구",
    periods: [{ label: "기간", duration: "2026.04.12. - 2026.04.14." }],
    type: "Learning Tool",
    visitUrl: "https://yt-script-maker.vercel.app/",
    image: "/images/youtube-script-detail-hero.webp",
    imageAlt: "Youtube Script Maker preview",
    galleryImages: [],
    tags: ["Next.js", "Javascript", "CSS", "Vercel", "Railway", "Gemini API"],
    summaries: [
      { label: "프로젝트 개요", text: "Next.js 기반 웹앱으로 구현하고, Railway의 transcript server로 자막 추출 문제를 분리해 해결했습니다." },
      { label: "주요 특징", text: "Gemini 다중 API 키 회전과 자동 재시도 로직을 적용해 긴 자막도 비교적 안정적으로 처리할 수 있도록 구성했습니다." },
      { label: "주요 기능", text: "유튜브 자막 추출, 학습 스크립트 생성, 중단 / 이어하기, PDF 다운로드, 반응형 UI를 구현했습니다." },
    ],
  },
  "portfolio-project": {
    title: "Portfolio",
    subtitle: "나만의 색깔과 작업 흐름을 담아낸 개인 포트폴리오 및 아카이브 웹사이트",
    periods: [{ label: "기간", duration: "2026.02. - 2026.03." }],
    type: "Archive",
    visitUrl: "https://jhi-portfolio.site/",
    image: "/images/portfolio-detail-hero.webp",
    imageAlt: "Portfolio preview",
    galleryImages: [{ src: "/images/portfolio-detail-sections.webp", alt: "Portfolio section overview" }],
    tags: ["HTML5", "CSS", "Javascript", "Spring Boot", "Thymeleaf", "AWS EC2", "MySQL"],
    summaries: [
      { label: "프로젝트 개요", text: "나만의 색깔을 담은 개인 포트폴리오이자, 프로젝트와 기록을 함께 정리할 수 있는 아카이브 웹사이트를 개발했습니다." },
      { label: "주요 특징", text: "Thymeleaf 기반 SSR 페이지와 Swup.js 전환을 적용해 자연스러운 탐색 경험을 구현했습니다." },
      { label: "주요 기능", text: "자기소개, 기술 스택, 프로젝트 상세 페이지, MySQL 기반 방명록 기능과 배포 자동화를 구성했습니다." },
    ],
  },
  "yourfit-project": {
    title: "뷰티샵 유어핏",
    subtitle: "부산에 새롭게 오픈한 뷰티 왁싱샵의 로고, 명함, 스탬프 카드 디자인을 제작했습니다. 10년 경력을 가지신 원장님이 유지한 컨셉인 바니의 이미지에 맞추어 귀여운 디자인에 유의했습니다. 판촉몰 전체에 통일감을 갖게 해, 매력이 전해지도록 고민했습니다.",
    periods: [
      { label: "기획", duration: "1주" },
      { label: "디자인", duration: "2주" },
    ],
    type: "Design",
    visitUrl: "",
    image: "/images/yourfit-detail-hero.webp",
    imageAlt: "뷰티샵 유어핏 매장 사인보드 시안",
    galleryImages: [
      { src: "/images/yourfit-board-display.webp", alt: "유어핏 매장 사인보드 시안" },
      { src: "/images/yourfit-poster-display.webp", alt: "유어핏 오픈 이벤트 포스터" },
      { src: "/images/yourfit-stamp-card-display.webp", alt: "유어핏 스탬프 카드 디자인" },
      { src: "/images/yourfit-business-card-display.webp", alt: "유어핏 명함 디자인" },
    ],
    tags: ["Canva", "Procreate"],
    summaries: [
      { label: "프로젝트 개요", text: "뷰티샵 유어핏의 로고와 캐릭터 그래픽을 중심으로 매장 경험에 필요한 인쇄물과 사인 시스템을 구성했습니다." },
      { label: "디자인 방향", text: "크림 톤의 종이 질감, 부드러운 핑크 포인트, 손그림 토끼 그래픽을 활용해 친근하고 편안한 뷰티샵 이미지를 만들었습니다." },
      { label: "제작 범위", text: "오픈 이벤트 포스터, 스탬프 카드, 명함, 외부 사인보드 시안을 하나의 브랜드 톤으로 연결했습니다." },
    ],
  },
};

await rm(distDir, { recursive: true, force: true });
await mkdir(distDir, { recursive: true });
await cp(staticDir, distDir, { recursive: true });

const commonScript = await readCommonScript();
const indexSource = await readFile(path.join(templatesDir, "index.html"), "utf8");
await writeFile(path.join(distDir, "index.html"), renderIndex(indexSource, commonScript));

for (const [slug, project] of Object.entries(projects)) {
  const projectDir = path.join(distDir, "work", slug);
  await mkdir(projectDir, { recursive: true });
  await writeFile(path.join(projectDir, "index.html"), renderProjectDetail(project, commonScript));
}

function renderIndex(source, commonScript) {
  let html = source
    .replace(' xmlns:th="http://www.thymeleaf.org"', "")
    .replace(/<div th:replace="~\{fragments\/nav :: mainMenu\('home'\)\}"><\/div>/, navHtml)
    .replace(/<div th:replace="~\{fragments\/footer :: siteFooter\}"><\/div>/, footerHtml)
    .replace(/<script th:replace="~\{fragments\/script :: commonJs\}"><\/script>/, `<script>${commonScript}</script>`)
    .replace(/ th:href="@\{([^}]+)\}"/g, ' href="$1"')
    .replace(/ th:src="@\{([^}]+)\}"/g, ' src="$1"')
    .replace(/ th:object="\$\{guestForm\}"/g, "")
    .replace(/ th:field="\*\{name\}"/g, ' name="name"')
    .replace(/ th:field="\*\{password\}"/g, ' name="password"')
    .replace(/ th:field="\*\{message\}"/g, ' name="message"')
    .replace(/ th:if="\$\{successMessage\}" th:text="\$\{successMessage\}"/g, "")
    .replace(/ th:if="\$\{errorMessage\}" th:text="\$\{errorMessage\}"/g, "");

  html = html.replace(
    '<form class="guest-form" action="/guest/add" method="post">',
    '<form class="guest-form" action="/api/guestbook" method="post" data-guestbook-api="true">'
  );

  html = html.replace(
    /<input type="hidden" name="submittedAt" th:value="\$\{#dates\.createNow\(\)\.time\}" \/>/,
    `<input type="hidden" name="submittedAt" value="" />`
  );

  html = html.replace(
    /<div class="guest-list">[\s\S]*?<\/article>\s*<\/div>/,
    `<div class="guest-list" data-guestbook-api="true">
         <p class="guest-empty">방명록을 불러오는 중입니다.</p>
        </div>`
  );

  return html.replace("</body>", `<script>document.querySelector('input[name="submittedAt"]')?.setAttribute("value", String(Date.now()));</script>\n </body>`);
}

async function readCommonScript() {
  const source = await readFile(path.join(templatesDir, "fragments", "script.html"), "utf8");
  return source
    .replace(/^<script[^>]*>\s*/, "")
    .replace(/\s*<\/script>\s*$/, "");
}

function renderProjectDetail(project, commonScript) {
  const shellClass = [
    "detail-shell",
    "is-detail",
    project.title === "Portfolio" ? "portfolio-project" : "",
    project.galleryImages.length === 0 ? "no-wide-visual" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return `<!doctype html>
<html lang="ko">
 <head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>${escapeHtml(project.title)} | portfolio</title>
  <link rel="icon" href="/favicon.png" type="image/x-icon" />
  <link rel="stylesheet" href="/css/style.css" />
 </head>
 <body class="detail-page">
  <main id="swup">
   <div class="${shellClass}">
    ${navHtml}
    <article class="detail-canvas transition-fade">
     <header class="detail-hero">
      <div class="detail-copy">
       <p class="detail-label">${project.type === "Design" ? "# design" : "# website"}</p>
       <h1 class="detail-title">${escapeHtml(project.title)}</h1>
       ${project.visitUrl ? `<a href="${project.visitUrl}" target="_blank" rel="noopener" class="detail-visit-link">Visit site</a>` : ""}
      </div>
      <figure class="detail-hero-visual">
       <picture>
        <img src="${project.image}" alt="${escapeHtml(project.imageAlt)}" />
       </picture>
      </figure>
     </header>
     <section class="detail-intro">
      <p class="detail-subtitle">${escapeHtml(project.subtitle)}</p>
     </section>
     ${renderWideVisual(project)}
     <section class="detail-info-list" aria-label="Project information">
      ${project.summaries
        .map(
          (summary) => `<article class="detail-info-row">
       <h2 class="detail-info-label">${escapeHtml(summary.label)}</h2>
       <p class="detail-info-text">${escapeHtml(summary.text)}</p>
      </article>`
        )
        .join("\n      ")}
      <article class="detail-info-row">
       <h2 class="detail-info-label">제작 기간</h2>
       <div class="detail-info-stack">
        ${project.periods.map((period) => `<p><span>${escapeHtml(period.label)}</span><span>${escapeHtml(period.duration)}</span></p>`).join("\n        ")}
       </div>
      </article>
      <article class="detail-info-row">
       <h2 class="detail-info-label">사용 도구</h2>
       <p class="detail-info-tools">${project.tags.map((tag) => `<span>${escapeHtml(tag)}</span>`).join("")}</p>
      </article>
     </section>
    </article>
   </div>
   ${footerHtml}
  </main>
  <script src="https://unpkg.com/swup@4"></script>
  <script>${commonScript}</script>
 </body>
</html>`;
}

function renderWideVisual(project) {
  if (project.title === "Portfolio") {
    return `<section class="detail-wide-visual" aria-label="Project preview">
      <picture class="detail-wide-picture">
       <img src="/images/portfolio-detail-sections.webp" alt="Portfolio section overview" loading="lazy" />
      </picture>
     </section>`;
  }

  if (project.galleryImages.length === 1) {
    const image = project.galleryImages[0];
    return `<section class="detail-wide-visual" aria-label="Project preview">
      <picture class="detail-wide-picture">
       <img src="${image.src}" alt="${escapeHtml(image.alt)}" loading="lazy" />
      </picture>
     </section>`;
  }

  if (project.galleryImages.length > 1) {
    return `<section class="detail-wide-visual" aria-label="Project preview">
      <div class="detail-gallery">
       <div class="gallery-frame" data-gallery>
        <div class="gallery-track" aria-label="Design project images">
         ${project.galleryImages.map((image) => `<figure class="gallery-card"><img src="${image.src}" alt="${escapeHtml(image.alt)}" loading="lazy" /></figure>`).join("\n         ")}
        </div>
        <div class="gallery-controls" aria-label="Gallery controls">
         <button type="button" class="gallery-btn gallery-btn-prev" data-gallery-prev aria-label="Previous image"></button>
         <span class="gallery-dots" data-gallery-dots aria-label="Gallery pages"></span>
         <span class="gallery-status" aria-live="polite"><span data-gallery-current>1</span>/<span data-gallery-total>1</span></span>
         <button type="button" class="gallery-btn gallery-btn-next" data-gallery-next aria-label="Next image"></button>
        </div>
       </div>
      </div>
     </section>`;
  }

  return "";
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}
