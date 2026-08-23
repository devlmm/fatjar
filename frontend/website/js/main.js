/*  ====================================================================================
    fatjar 官网交互脚本 main.js
    ------------------------------------------------------------------------------------
    功能：
      1. 自动注入当前年份到页脚（避免每年手动改）
      2. 平滑滚动：点击导航锚点平滑滚动到目标区块
      3. 导航高亮：滚动时根据当前可视区块高亮对应菜单
      4. 导航栏滚动阴影：滚动一定距离后给 navbar 加阴影
      5. 移动端菜单切换：点击汉堡按钮展开/收起菜单
    说明：原生 JS 实现，无任何依赖；DOMContentLoaded 后执行
    ==================================================================================== */

// 等待 DOM 解析完毕后执行（避免元素未就绪）
document.addEventListener('DOMContentLoaded', function () {

    /* ---------- 1. 页脚年份自动注入 ---------- */
    var yearEl = document.getElementById('year');
    if (yearEl) {
        yearEl.textContent = new Date().getFullYear();
    }

    /* ---------- 2. 平滑滚动 + 移动端菜单收起 ---------- */
    // 获取所有带 href="#xxx" 的链接
    var anchorLinks = document.querySelectorAll('a[href^="#"]');
    anchorLinks.forEach(function (link) {
        link.addEventListener('click', function (e) {
            var targetId = this.getAttribute('href');
            // 跳过仅 "#" 的占位链接
            if (targetId === '#' || targetId.length < 2) return;
            var target = document.querySelector(targetId);
            if (target) {
                e.preventDefault();
                // scrollIntoView 配合 CSS scroll-behavior: smooth 实现平滑滚动
                // 顶部预留 64px 避免被固定 navbar 遮挡
                var top = target.getBoundingClientRect().top + window.pageYOffset - 64;
                window.scrollTo({ top: top, behavior: 'smooth' });
                // 移动端点击后收起菜单
                closeMobileMenu();
            }
        });
    });

    /* ---------- 3. 导航高亮 + 滚动阴影 ---------- */
    var navbar = document.getElementById('navbar');
    var navLinks = document.querySelectorAll('.nav-link');
    // 需要监听的区块（与菜单 href 对应）
    var sections = ['hero', 'features', 'techstack', 'modules']
        .map(function (id) { return document.getElementById(id); })
        .filter(Boolean);

    // 节流：滚动事件高频触发，用 requestAnimationFrame 节流
    var ticking = false;
    window.addEventListener('scroll', function () {
        if (!ticking) {
            window.requestAnimationFrame(function () {
                updateNavbar();
                ticking = false;
            });
            ticking = true;
        }
    });

    /**
     * 根据 scrollY 更新导航栏样式与高亮菜单
     */
    function updateNavbar() {
        var scrollY = window.pageYOffset;
        // 滚动超过 10px 给 navbar 加阴影
        if (navbar) {
            if (scrollY > 10) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        }
        // 找到当前可视区域中"最靠上"的区块
        var currentId = 'hero';
        var offset = 100; // 偏移量：进入区块顶部 100px 触发高亮
        sections.forEach(function (sec) {
            if (sec.getBoundingClientRect().top <= offset) {
                currentId = sec.id;
            }
        });
        // 高亮对应菜单（移除其他菜单的 active）
        navLinks.forEach(function (link) {
            var href = link.getAttribute('href');
            if (href === '#' + currentId) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });
    }
    // 初始化执行一次
    updateNavbar();

    /* ---------- 4. 移动端菜单切换 ---------- */
    var navToggle = document.getElementById('navToggle');
    var navMenu = document.querySelector('.nav-menu');
    if (navToggle && navMenu) {
        navToggle.addEventListener('click', function () {
            navMenu.classList.toggle('nav-open');
        });
    }
    /**
     * 关闭移动端菜单（点击菜单项后调用）
     */
    function closeMobileMenu() {
        if (navMenu && navMenu.classList.contains('nav-open')) {
            navMenu.classList.remove('nav-open');
        }
    }

    /* ---------- 5. 控制台彩蛋：开发者提示 ---------- */
    console.log(
        '%c fatjar 企业云原生大单体业务管理系统脚手架 %c v1.0.0 ',
        'background:#1677ff;color:#fff;padding:4px 8px;border-radius:4px 0 0 4px;',
        'background:#0958d9;color:#fff;padding:4px 8px;border-radius:0 4px 4px 0;'
    );
    console.log('后端 API 文档：http://localhost:8080/doc.html');
    console.log('管理后台：http://localhost:5173');
});
