document.addEventListener("DOMContentLoaded", function () {

  // === 메인 슬라이드 === //
  // const carousel = document.querySelector("#mainSlide");
  // const bsCarousel = new bootstrap.Carousel(carousel, {
  //   interval: 3000,
  //   pause: false,
  // });

  // const progress = document.querySelector(".progress-bar .progress");
  // const carouselToggleBtn = document.getElementById("togglePlay");
  // let isCarouselPlaying = true;

  // // 인디케이터 버튼들
  // const indicatorBtns = document.querySelectorAll(".number-indicators button");

  // // 슬라이드 이동 이벤트
  // carousel.addEventListener("slide.bs.carousel", (e) => {
  //   // 프로그래스바 리셋
  //   progress.style.transition = "none";
  //   progress.style.width = "0%";
  //   setTimeout(() => {
  //     progress.style.transition = "width 3s linear";
  //     progress.style.width = "100%";
  //   }, 50);

  //   // 인디케이터 업데이트
  //   indicatorBtns.forEach((btn) => btn.classList.remove("active"));
  //   indicatorBtns[e.to].classList.add("active");
  // });

  // // 재생/정지 버튼
  // carouselToggleBtn.addEventListener("click", () => {
  //   if (isCarouselPlaying) {
  //     bsCarousel.pause();
  //     carouselToggleBtn.innerHTML = '<i class="bi bi-play"></i>';
  //   } else {
  //     bsCarousel.cycle();
  //     carouselToggleBtn.innerHTML = '<i class="bi bi-pause"></i>';
  //   }
  //   isCarouselPlaying = !isCarouselPlaying;
  // });

  // // 초기 실행
  // progress.style.width = "100%";

  // === 공지사항 롤링 === //
  var swiper1 = new Swiper("#noticeRolling .mySwiper", {
    direction: "vertical",
    loop: true,
    autoplay: {
      delay: 3000,
      disableOnInteraction: false,
    },
    navigation: {
      nextEl: "#swiperNext",
      prevEl: "#swiperPrev",
    },
  });

  const swiperToggleBtn = document.getElementById("swiperToggle");
  let isSwiperPlaying = true;

  swiperToggleBtn.addEventListener("click", () => {
    if (isSwiperPlaying) {
      swiper1.autoplay.stop();
      swiperToggleBtn.innerHTML = '<i class="bi bi-play-fill"></i>';
    } else {
      swiper1.autoplay.start();
      swiperToggleBtn.innerHTML = '<i class="bi bi-pause-fill"></i>';
    }
    isSwiperPlaying = !isSwiperPlaying;
  });

  // === 추천강좌 === //
  var swiper = new Swiper("#recommend .mySwiper", {
    centeredSlides: true,
    loop: true,
    pagination: {
      el: "#recommend .swiper-pagination",
      clickable: true,
    },
    navigation: {
      nextEl: "#recommendNext",
      prevEl: "#recommendPrev",
    },
    breakpoints: {
      768: {
        slidesPerView: 3,
      },
      480: {
        slidesPerView: 3,
      }
    }
  });

  // === 인기 강좌 === //
  var swiper2 = new Swiper("#popular .mySwiper", {
    navigation: {
      nextEl: "#popularNext",
      prevEl: "#popularPrev",
    },
    //slidesPerView: "auto",
    spaceBetween: 20,
    // centeredSlides: true,
    // centeredSlidesBounds: true,
    breakpoints: {
      991: {
        slidesPerView: 4,
      },
       768: {
        slidesPerView: 3,
      },
      480: {
        slidesPerView: 2,
      }
    }
  });

  // 배경 색상 변경
  // const popularSection = document.querySelector("#popular");
  // const observer = new IntersectionObserver(
  //   (entries) => {
  //     entries.forEach((entry) => {
  //       if (entry.isIntersecting) {
  //         popularSection.classList.add("active");
  //       } else {
  //         popularSection.classList.remove("active");
  //       }
  //     });
  //   },
  //   { threshold: 0.3 }
  // );
  // observer.observe(popularSection);

 
});
