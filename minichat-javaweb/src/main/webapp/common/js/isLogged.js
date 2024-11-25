
const isLogin = CookieUtil.hasCookie("uname") && CookieUtil.hasCookie("uid");

if (window.location.pathname === "/") {
  // 在登录界面并且已经登录，则直接跳转到联系人页面
  if (isLogin) {
    window.location.href = "/view/Contact/index.html";
  }
} else {
  // 不在登录界面并且未登录，则直接跳转到登录页面
  if (!isLogin) {
    window.location.href = "/";
  }
}