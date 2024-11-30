const forms = document.querySelector(".forms");
const pwShowHide = document.querySelectorAll(".eye-icon");
const links = document.querySelectorAll(".link");

// Tampilkan/matikan password
pwShowHide.forEach(eyeIcon => {
    eyeIcon.addEventListener("click", () => {
        const pwFields = eyeIcon.closest(".field").querySelectorAll(".password");
        pwFields.forEach(password => {
            if (password.type === "password") {
                password.type = "text";
                eyeIcon.classList.replace("bx-hide", "bx-show");
            } else {
                password.type = "password";
                eyeIcon.classList.replace("bx-show", "bx-hide");
            }
        });
    });
});

// Navigasi antara login dan signup
links.forEach(link => {
    link.addEventListener("click", e => {
        e.preventDefault();
        forms.classList.toggle("show-signup");
    });
});

// Validasi login
document.getElementById("login-form").addEventListener("submit", e => {
    e.preventDefault();
    const username = document.getElementById("login-username").value.trim();
    const password = document.getElementById("login-password").value.trim();

    if (!username || !password) {
        alert("Username dan password harus diisi!");
        return;
    }

    // Periksa apakah data sesuai
    const storedUser = JSON.parse(localStorage.getItem("userData"));
    if (storedUser && storedUser.username === username && storedUser.password === password) {
        alert("Login berhasil!");
        // Alihkan ke dashboard
        window.location.href = "dashboard.html"; // Ganti dengan URL dashboard Anda
    } else {
        alert("Akun tidak ditemukan! Silakan daftar terlebih dahulu.");
        // Alihkan ke formulir signup
        forms.classList.add("show-signup");
    }
});

// Validasi signup
document.getElementById("signup-form").addEventListener("submit", e => {
    e.preventDefault();
    const username = e.target.username.value.trim();
    const email = e.target.email.value.trim();
    const phone = e.target.phone.value.trim();
    const password = e.target.password.value;
    const confirmPassword = e.target.confirm_password.value;

    if (!username || !email || !phone || !password || !confirmPassword) {
        alert("Semua field harus diisi!");
        return;
    }

    if (password !== confirmPassword) {
        alert("Password dan konfirmasi password tidak cocok!");
        return;
    }

    // Simpan data ke localStorage (atau kirim ke server)
    const userData = {
        username,
        email,
        phone,
        password,
    };
    localStorage.setItem("userData", JSON.stringify(userData));

    alert("Registrasi berhasil!");

    // Reset input formulir signup
    e.target.reset();


    // Kembali ke formulir login
    forms.classList.remove("show-signup");
});