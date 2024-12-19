// src/main/resources/static/js/addlapangan.js

// Mendapatkan elemen-elemen
const addFieldButton = document.getElementById("add-field");
const popup = document.getElementById("addFieldPopup");
const closeButton = document.querySelector(".popup-close");
const addFieldForm = document.getElementById("addFieldForm");

// Fungsi untuk mendapatkan token CSRF dari meta tags
function getCsrfToken() {
  const csrfToken = document
    .querySelector('meta[name="_csrf"]')
    .getAttribute("content");
  const csrfHeader = document
    .querySelector('meta[name="_csrf_header"]')
    .getAttribute("content");
  return { csrfToken, csrfHeader };
}

// Menampilkan popup dengan logging
addFieldButton.addEventListener("click", () => {
  console.log("Add Field button clicked");
  popup.classList.add("show");
  popup.classList.remove("hidden");
});

// Menutup popup dengan logging
closeButton.addEventListener("click", () => {
  console.log("Close popup button clicked");
  popup.classList.remove("show");
  setTimeout(() => {
    popup.classList.add("hidden");
  }, 300);
});

// Menangani pengiriman formulir dengan logging
addFieldForm.addEventListener("submit", (event) => {
  event.preventDefault();
  console.log("Form submitted");

  // Mendapatkan nilai dari formulir
  const fieldName = document.getElementById("field-name").value;
  const fieldLocation = document.getElementById("field-location").value;
  const fieldType = document.getElementById("field-type").value;
  const fieldDescription = document.getElementById("field-description").value;
  const fieldPrice = parseFloat(document.getElementById("field-price").value);
  const fieldRating = parseFloat(document.getElementById("field-rating").value);

  // Mendapatkan fasilitas yang dipilih
  const facilities = Array.from(
    document.querySelectorAll('input[name="fieldFacilities"]:checked')
  ).map((checkbox) => checkbox.value);

  // Menyiapkan data
  const data = {
    namaLapangan: fieldName,
    city: fieldLocation,
    cabangOlahraga: fieldType,
    deskripsiLapangan: fieldDescription,
    price: fieldPrice,
    rating: fieldRating,
    facilities: facilities, // Karena di server kita ubah menjadi string
  };

  console.log("Mengirim data:", data); // Log untuk debugging

  // Mendapatkan token CSRF
  const { csrfToken, csrfHeader } = getCsrfToken();
  console.log("CSRF Token:", csrfToken, "Header:", csrfHeader);

  // Mengirim data ke server
  fetch("/admin/lapangan/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      [csrfHeader]: csrfToken, // Sertakan token CSRF
    },
    body: JSON.stringify(data),
  })
    .then((response) => {
      console.log("Response status:", response.status);
      if (response.ok) {
        console.log("Lapangan berhasil ditambahkan.");
        // Menutup popup dan me-refresh tabel
        popup.classList.remove("show");
        setTimeout(() => {
          popup.classList.add("hidden");
          location.reload();
        }, 300);
      } else {
        response.text().then((text) => {
          console.error("Gagal menambahkan lapangan:", text);
          alert("Gagal menambahkan lapangan: " + text);
        });
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Terjadi kesalahan.");
    });

  // Reset formulir
  addFieldForm.reset();
});

// Menutup popup saat klik di luar konten popup dengan logging
popup.addEventListener("click", (event) => {
  if (event.target === popup) {
    console.log("Clicked outside the popup content");
    popup.classList.remove("show");
    setTimeout(() => {
      popup.classList.add("hidden");
    }, 300);
  }
});
