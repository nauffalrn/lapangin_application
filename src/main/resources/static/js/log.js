// Toggle Password Visibility
document.addEventListener("DOMContentLoaded", () => {
  // Toggle Password Visibility
  const pwShowHide = document.querySelectorAll(".eye-icon");

  pwShowHide.forEach((eyeIcon) => {
    eyeIcon.addEventListener("click", () => {
      const pwField = eyeIcon.previousElementSibling;

      if (pwField.type === "password") {
        pwField.type = "password";
        eyeIcon.classList.replace("bx-hide", "bx-show");
      } else {
        pwField.type = "text";
        eyeIcon.classList.replace("bx-show", "bx-hide");
      }
    });
  });

  // Validasi Form Login
  const loginForm = document.getElementById("login-form");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      // Reset pesan error sebelumnya
      const loginGeneralError = document.getElementById("login-general-error");
      const loginUsernameError = document.getElementById(
        "login-username-error"
      );
      const loginPasswordError = document.getElementById(
        "login-password-error"
      );

      if (loginGeneralError) loginGeneralError.textContent = "";
      if (loginUsernameError) loginUsernameError.textContent = "";
      if (loginPasswordError) loginPasswordError.textContent = "";

      // Tambahkan validasi tambahan jika diperlukan
    });
  }

  // Validasi Form Registrasi
  const registrationForm = document.getElementById("registration-form");
  if (registrationForm) {
    registrationForm.addEventListener("submit", (e) => {
      console.log("Form Registrasi Dikirim"); // Logging untuk debugging

      // Reset pesan error sebelumnya
      const generalError = document.getElementById("register-general-error");
      const generalError2 = document.getElementById("register-general-error-2");
      const passwordError = document.getElementById("register-password-error");
      const confirmPasswordError = document.getElementById(
        "register-confirm-password-error"
      );

      if (generalError) generalError.textContent = "";
      if (generalError2) generalError2.textContent = "";
      if (passwordError) {
        passwordError.textContent = "";
        passwordError.style.display = "none";
      }
      if (confirmPasswordError) {
        confirmPasswordError.textContent = "";
        confirmPasswordError.style.display = "none";
      }

      // Validasi panjang password
      const passwordField = document.getElementById("field-password");
      const password = passwordField ? passwordField.value.trim() : "";
      console.log("Password:", password); // Logging nilai password

      let isValid = true;

      if (password.length < 8) {
        console.log("Password kurang dari 8 karakter"); // Logging jika kurang dari 8
        if (passwordError) {
          passwordError.textContent = "Password harus minimal 8 karakter.";
          passwordError.style.display = "block";
        }
        isValid = false;
      }

      // Validasi konfirmasi password
      const confirmPasswordField = document.getElementById(
        "field-confirmPassword"
      );
      const confirmPassword = confirmPasswordField
        ? confirmPasswordField.value.trim()
        : "";
      console.log("Confirm Password:", confirmPassword); // Logging nilai confirmPassword

      if (password !== confirmPassword) {
        console.log("Password dan Confirm Password tidak cocok"); // Logging jika tidak cocok
        if (confirmPasswordError) {
          confirmPasswordError.textContent =
            "Password dan Confirm Password tidak cocok.";
          confirmPasswordError.style.display = "block";
        }
        isValid = false;
      }

      // Jika validasi gagal, hentikan pengiriman form
      if (!isValid) {
        e.preventDefault();
      }
    });
  }

  // Menangani Pesan Error dari Server
  const serverErrorElements = document.querySelectorAll(".error-message p");
  serverErrorElements.forEach((element) => {
    if (element.textContent.trim() !== "") {
      // Menampilkan pesan error dari server
      element.parentElement.style.display = "block";
    } else {
      // Menyembunyikan jika tidak ada pesan error
      element.parentElement.style.display = "none";
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const profileForm = document.getElementById("profileForm");
  const profileImageInput = document.getElementById("profileImage");
  const profilePreview = document.getElementById("profilePreview");
  const cancelButton = document.getElementById("cancelChanges");

  if (profileForm && profileImageInput && profilePreview && cancelButton) {
    // Simpan data asli untuk cancel
    const originalData = {
      username: document.getElementById("username").value,
      email: document.getElementById("email").value,
      phone: document.getElementById("phone").value,
      profileImageSrc: profilePreview.src,
    };

    // Preview Gambar
    profileImageInput.addEventListener("change", (event) => {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          profilePreview.src = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    });

    // Cancel Perubahan
    cancelButton.addEventListener("click", () => {
      document.getElementById("username").value = originalData.username;
      document.getElementById("email").value = originalData.email;
      document.getElementById("phone").value = originalData.phone;
      profilePreview.src = originalData.profileImageSrc;
      // Reset file input
      profileImageInput.value = "";
      // Clear error messages
      document.getElementById("usernameError").textContent = "";
      document.getElementById("emailError").textContent = "";
      document.getElementById("phoneError").textContent = "";
    });

    // Validasi dan Submit Form
    profileForm.addEventListener("submit", (e) => {
      // Clear pesan error sebelumnya
      document.getElementById("usernameError").textContent = "";
      document.getElementById("emailError").textContent = "";
      document.getElementById("phoneError").textContent = "";

      let isValid = true;

      // Validasi Username
      const username = document.getElementById("username").value.trim();
      if (username.length < 3) {
        isValid = false;
        document.getElementById("usernameError").textContent =
          "Username harus minimal 3 karakter.";
      }

      // Validasi Email
      const email = document.getElementById("email").value.trim();
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(email)) {
        isValid = false;
        document.getElementById("emailError").textContent =
          "Format email tidak valid.";
      }

      // Validasi No Telepon
      const phone = document.getElementById("phone").value.trim();
      const phonePattern = /^\d{10,15}$/;
      if (!phonePattern.test(phone)) {
        isValid = false;
        document.getElementById("phoneError").textContent =
          "Nomor telepon harus 10-15 digit.";
      }

      if (!isValid) {
        e.preventDefault();
      }
    });
  }

  // Toggle Password Visibility (Jika Diperlukan)
  const pwShowHide = document.querySelectorAll(".eye-icon");
  pwShowHide.forEach((eyeIcon) => {
    eyeIcon.addEventListener("click", () => {
      const pwField = eyeIcon.previousElementSibling;
      if (pwField.type === "password") {
        pwField.type = "text";
        eyeIcon.classList.replace("bx-hide", "bx-show");
      } else {
        pwField.type = "password";
        eyeIcon.classList.replace("bx-show", "bx-hide");
      }
    });
  });

  // Validasi Form Login
  const loginForm = document.getElementById("login-form");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      // Reset pesan error sebelumnya
      const loginGeneralError = document.getElementById("login-general-error");
      const loginUsernameError = document.getElementById(
        "login-username-error"
      );
      const loginPasswordError = document.getElementById(
        "login-password-error"
      );
      if (loginGeneralError) loginGeneralError.textContent = "";
      if (loginUsernameError) loginUsernameError.textContent = "";
      if (loginPasswordError) loginPasswordError.textContent = "";
      // Pastikan tidak ada e.preventDefault() di sini
    });
  }

  // Validasi Form Registrasi
  const registrationForm = document.getElementById("registration-form");
  if (registrationForm) {
    registrationForm.addEventListener("submit", (e) => {
      console.log("Form Registrasi Dikirim"); // Logging untuk debugging

      // Reset pesan error sebelumnya
      const generalError = document.getElementById("register-general-error");
      const generalError2 = document.getElementById("register-general-error-2");
      const passwordError = document.getElementById("register-password-error");
      const confirmPasswordError = document.getElementById(
        "register-confirm-password-error"
      );

      if (generalError) generalError.textContent = "";
      if (generalError2) generalError2.textContent = "";
      if (passwordError) {
        passwordError.textContent = "";
        passwordError.style.display = "none";
      }
      if (confirmPasswordError) {
        confirmPasswordError.textContent = "";
        confirmPasswordError.style.display = "none";
      }

      // Validasi panjang password
      const passwordField = document.getElementById("field-password");
      const password = passwordField ? passwordField.value.trim() : "";
      console.log("Password:", password); // Logging nilai password

      let isValid = true;

      if (password.length < 8) {
        console.log("Password kurang dari 8 karakter"); // Logging jika kurang dari 8
        if (passwordError) {
          passwordError.textContent = "Password harus minimal 8 karakter.";
          passwordError.style.display = "block"; // Pastikan error ditampilkan
        }
        isValid = false;
      }

      // Validasi konfirmasi password
      const confirmPasswordField = document.getElementById(
        "field-confirmPassword"
      );
      const confirmPassword = confirmPasswordField
        ? confirmPasswordField.value.trim()
        : "";
      console.log("Confirm Password:", confirmPassword); // Logging nilai confirmPassword

      if (password !== confirmPassword) {
        console.log("Password dan Confirm Password tidak cocok"); // Logging jika tidak cocok
        if (confirmPasswordError) {
          confirmPasswordError.textContent = "Password tidak cocok.";
          confirmPasswordError.style.display = "block"; // Pastikan error ditampilkan
        }
        isValid = false;
      }

      // Jika validasi gagal, hentikan pengiriman form
      if (!isValid) {
        e.preventDefault();
      }
    });
  }

  // Menangani Pesan Error dari Server
  const serverErrorElements = document.querySelectorAll(".error-message p");
  serverErrorElements.forEach((element) => {
    if (element.textContent.trim() !== "") {
      // Menampilkan pesan error dari server
      element.parentElement.style.display = "block";
    } else {
      // Menyembunyikan jika tidak ada pesan error
      element.parentElement.style.display = "none";
    }
  });
});
