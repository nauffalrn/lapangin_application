// Data simulasi "database"
const users = {};

// Registrasi
document.getElementById("-form").addEventListener("submit", function (event) {
  event.preventDefault(); // Mencegah reload halaman

  // Ambil nilai dari form
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirm-password").value;
  const message = document.getElementById("message");

  // Reset pesan
  message.textContent = "";

  // Validasi input
  if (!username || !password || !confirmPassword) {
    message.textContent = "Semua bidang harus diisi.";
    return;
  }

  if (users[username]) {
    message.textContent = "Username sudah terdaftar.";
    return;
  }

  if (password !== confirmPassword) {
    message.textContent = "Password dan konfirmasi password tidak cocok.";
    return;
  }

  // Simpan pengguna baru ke "database"
  users[username] = password;

  message.textContent = "Registrasi berhasil! Silakan login.";
  message.className = "success";

  // Reset form
  document.getElementById("register-form").reset();
});

// Login
document.getElementById("login-form").addEventListener("submit", function (event) {
  event.preventDefault(); // Mencegah reload halaman

  // Ambil nilai dari form
  const username = document.getElementById("login-username").value.trim();
  const password = document.getElementById("login-password").value;
  const message = document.getElementById("login-message");

  // Reset pesan
  message.textContent = "";

  // Validasi input
  if (!username || !password) {
    message.textContent = "Username dan password tidak boleh kosong.";
    return;
  }

  if (!users[username]) {
    message.textContent = "Username tidak terdaftar.";
    return;
  }

  if (users[username] !== password) {
    message.textContent = "Password salah.";
    return;
  }

  // Login berhasil
  message.textContent = "Login berhasil!";
  message.className = "success";

  // Redirect simulasi (hanya alert di sini)
  setTimeout(() => {
    alert("Selamat datang, " + username + "!");
    document.getElementById("login-form").reset();
  }, 1000);
});
