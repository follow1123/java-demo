const loginForm = document.getElementById("loginForm");
loginForm.onsubmit = (e) => {
  // 禁用默认提交事件
  e.preventDefault();

  const formData = new FormData(e.target);
  const username = formData.get("username");
  const password = formData.get("password");
  const queryParams = new URLSearchParams({ username,  password});

  fetch(`http://localhost:8080/auth/login?${queryParams.toString()}`, { method: "POST" })
    .then(v => v.json())
    .then(reslut => {
      if (reslut.status) {
        window.location.href = "/view/Contact/index.html";
      } else {
        alert(reslut.message);
      }
    });
}