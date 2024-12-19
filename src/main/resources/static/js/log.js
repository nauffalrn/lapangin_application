// Tampilkan/Matiin password
const pwShowHide = document.querySelectorAll(".eye-icon");
pwShowHide.forEach((eyeIcon) => {
  eyeIcon.addEventListener("click", () => {
    const pwField = eyeIcon.previousElementSibling; // Ambil input password
    if (pwField.type === "password") {
      pwField.type = "text"; // Menampilkan password
      eyeIcon.classList.replace("bx-hide", "bx-show"); // Ganti ikon ke 'show'
    } else {
      pwField.type = "password"; // Menyembunyikan password
      eyeIcon.classList.replace("bx-show", "bx-hide"); // Ganti ikon ke 'hide'
    }
  });
});

// Validasi login
document.getElementById("login-form").addEventListener("submit", (e) => {
  const username = e.target.username.value.trim();
  const password = e.target.password.value.trim();

  // Cek apakah username dan password kosong
  if (!username || !password) {
    alert("Username dan password harus diisi!");
    e.preventDefault(); // Mencegah pengiriman formulir
    return;
  }

  // Jika validasi lulus, biarkan formulir dikirim
});

// Validasi signup
document.getElementById("registration-form").addEventListener("submit", (e) => {
  e.preventDefault(); // Mencegah form dikirim langsung
  const name = e.target.name.value.trim();
  const username = e.target.username.value.trim();
  const email = e.target.email.value.trim();
  const phone = e.target.phoneNumber.value.trim();
  const password = e.target.password.value;
  const confirmPassword = e.target.confirmPassword.value; // Sesuaikan nama field

  // Validasi format email menggunakan regex
  const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  if (!emailPattern.test(email)) {
    alert("Email tidak valid!");
    return;
  }

  // Validasi format nomor telepon menggunakan regex (bisa disesuaikan dengan kebutuhan)
  const phonePattern = /^[0-9]{10,15}$/;
  if (!phonePattern.test(phone)) {
    alert("Nomor telepon tidak valid!");
    return;
  }

  // Validasi kecocokan password dan konfirmasi password
  if (password !== confirmPassword) {
    alert("Password dan konfirmasi password tidak cocok!");
    return;
  }

  // Jika semua validasi lulus, kirim formulir
  e.target.submit();
});
