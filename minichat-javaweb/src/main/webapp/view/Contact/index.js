document.getElementById("username").innerText = CookieUtil.getCookie("uname");

let btnLogout = document.getElementById("logout")

btnLogout.onclick = (e) => {
  fetch("http://localhost:8080/auth/logout", { method: "post" })
    .then(v => v.json())
    .then(reslut => {
      if (reslut.status) {
        window.location.href = "/";
      } else {
        alert(reslut.message);
      }
    });
}