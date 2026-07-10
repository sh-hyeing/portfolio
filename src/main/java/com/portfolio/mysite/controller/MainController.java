package com.portfolio.mysite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.portfolio.mysite.entity.Guestbook;
import com.portfolio.mysite.service.GuestbookService;

import lombok.RequiredArgsConstructor;
import java.util.List; 
import java.util.Map;

import org.springframework.ui.Model; 

@Controller
@RequiredArgsConstructor
public class MainController {
    private final GuestbookService guestbookService; 

    @GetMapping("/")
    public String index(Model model) {
        List<Guestbook> guestList = guestbookService.getAllMessages();
        model.addAttribute("guestList", guestList);
        
        model.addAttribute("guestForm", new Guestbook());
        
        return "index"; 
    }

    @GetMapping("/about")
    public String about() {
        return "about"; 
    }

    @GetMapping("/work")
    public String work() {
        return "work"; 
    }

    @GetMapping("/work/{projectName}")
    public String getWorkDetail(@PathVariable String projectName, Model model) {
        ProjectDetail project = PROJECTS.get(projectName);
        if (project == null) {
            return "redirect:/#work";
        }

        model.addAttribute("project", project);
        return "project-detail";
    }

    private static final Map<String, ProjectDetail> PROJECTS = Map.of(
            "yt-script-project",
            new ProjectDetail(
                    "Youtube Script Maker",
                    "유튜브 영상 자막을 불러와 영어·한국어 학습용 스크립트로 정리하고 PDF로 저장할 수 있는 웹 기반 학습 도구",
                    List.of(new PeriodItem("기간", "2026.04.12. - 2026.04.14.")),
                    "Learning Tool",
                    "https://yt-script-maker.vercel.app/",
                    "/images/youtube-script-detail-hero.webp",
                    "",
                    "Youtube Script Maker preview",
                    List.of(),
                    List.of("Next.js", "Javascript", "CSS", "Vercel", "Railway", "Gemini API"),
                    List.of(
                            new SummaryItem("프로젝트 개요", "Next.js 기반 웹앱으로 구현하고, Railway의 transcript server로 자막 추출 문제를 분리해 해결했습니다."),
                            new SummaryItem("주요 특징", "Gemini 다중 API 키 회전과 자동 재시도 로직을 적용해 긴 자막도 비교적 안정적으로 처리할 수 있도록 구성했습니다."),
                            new SummaryItem("주요 기능", "유튜브 자막 추출, 학습 스크립트 생성, 중단 / 이어하기, PDF 다운로드, 반응형 UI를 구현했습니다.")
                    )
            ),
            "portfolio-project",
            new ProjectDetail(
                    "Portfolio",
                    "나만의 색깔과 작업 흐름을 담아낸 개인 포트폴리오 및 아카이브 웹사이트",
                    List.of(new PeriodItem("기간", "2026.02. - 2026.03.")),
                    "Archive",
                    "https://jhi-portfolio.site/",
                    "/images/portfolio-detail-hero.webp",
                    "",
                    "Portfolio preview",
                    List.of(
                            new GalleryImage("/images/portfolio-detail-sections.webp", "Portfolio section overview")
                    ),
                    List.of("HTML5", "CSS", "Javascript", "Spring Boot", "Thymeleaf", "Swup.js", "AWS EC2", "MySQL"),
                    List.of(
                            new SummaryItem("프로젝트 개요", "나만의 색깔을 담은 개인 포트폴리오이자, 프로젝트와 기록을 함께 정리할 수 있는 아카이브 웹사이트를 개발했습니다."),
                            new SummaryItem("주요 특징", "Thymeleaf 기반 SSR 페이지와 Swup.js 전환을 적용해 자연스러운 탐색 경험을 구현했습니다."),
                            new SummaryItem("주요 기능", "자기소개, 기술 스택, 프로젝트 상세 페이지, MySQL 기반 방명록 기능과 배포 자동화를 구성했습니다.")
                    )
            ),
            "yourfit-project",
            new ProjectDetail(
                    "뷰티샵 유어핏",
                    "부산에 새롭게 오픈한 뷰티 왁싱샵의 로고, 명함, 스탬프 카드 디자인을 제작했습니다. 10년 경력을 가지신 원장님이 유지한 컨셉인 바니의 이미지에 맞추어 귀여운 디자인에 유의했습니다. 판촉몰 전체에 통일감을 갖게 해, 매력이 전해지도록 고민했습니다.",
                    List.of(
                            new PeriodItem("기획", "1주"),
                            new PeriodItem("디자인", "2주")
                    ),
                    "Design",
                    "",
                    "/images/yourfit-detail-hero.webp",
                    "",
                    "뷰티샵 유어핏 매장 사인보드 시안",
                    List.of(
                            new GalleryImage("/images/yourfit-board-display.webp", "유어핏 매장 사인보드 시안"),
                            new GalleryImage("/images/yourfit-poster-display.webp", "유어핏 오픈 이벤트 포스터"),
                            new GalleryImage("/images/yourfit-stamp-card-display.webp", "유어핏 스탬프 카드 디자인"),
                            new GalleryImage("/images/yourfit-business-card-display.webp", "유어핏 명함 디자인")
                    ),
                    List.of("Canva", "Procreate"),
                    List.of(
                            new SummaryItem("프로젝트 개요", "뷰티샵 유어핏의 로고와 캐릭터 그래픽을 중심으로 매장 경험에 필요한 인쇄물과 사인 시스템을 구성했습니다."),
                            new SummaryItem("디자인 방향", "크림 톤의 종이 질감, 부드러운 핑크 포인트, 손그림 토끼 그래픽을 활용해 친근하고 편안한 뷰티샵 이미지를 만들었습니다."),
                            new SummaryItem("제작 범위", "오픈 이벤트 포스터, 스탬프 카드, 명함, 외부 사인보드 시안을 하나의 브랜드 톤으로 연결했습니다.")
                    )
            )
    );

    public record ProjectDetail(
            String title,
            String subtitle,
            List<PeriodItem> periods,
            String type,
            String visitUrl,
            String imagePng,
            String imageWebp,
            String imageAlt,
            List<GalleryImage> galleryImages,
            List<String> tags,
            List<SummaryItem> summaries
    ) {
    }

    public record GalleryImage(String src, String alt) {
    }

    public record PeriodItem(String label, String duration) {
    }

    public record SummaryItem(String label, String text) {
    }

}
