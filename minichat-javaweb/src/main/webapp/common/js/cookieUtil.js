class CookieUtil {
  static hasCookie = (name) => {
    let idx = document.cookie.indexOf(name);
    return idx !== -1 &&
      (idx == 0 || document.cookie[idx - 1] === ' ') &&
      document.cookie[idx + name.length] === '=';
  }
  static getCookie = (name) => {
    if (!this.hasCookie(name)) return null;
    let idx = document.cookie.indexOf(name);
    let ck = document.cookie;
    let value = "";
    for (let i = idx + name.length + 1; i < ck.length; i++) {
      if (ck[i] === ';') break;
      value += ck[i];
    }
    return value;
  }
}