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

    // Logika validasi tanpa menyentuh localStorage
    // Kirim ke server side untuk validasi melalui form post jika diperlukan
    alert("Form login akan dikirim ke server untuk validasi");
    e.target.submit(); // Mengirim formulir login untuk diproses oleh server
});

// Validasi signup
document.getElementById("signup-form").addEventListener("submit", e => {
    e.preventDefault();
    const username = e.target.username.value.trim();
    const email = e.target.email.value.trim();
    const phone = e.target.phoneNumber.value.trim();
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

    // Mengirim form signup langsung ke server
    alert("Form signup akan dikirim ke server untuk registrasi");
    e.target.submit(); // Mengirim formulir signup untuk diproses oleh server
});