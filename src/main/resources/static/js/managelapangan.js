// Mendapatkan elemen-elemen
const addFieldButton = document.getElementById("add-field");
const addPopup = document.getElementById("addFieldPopup");
const addCloseButton = document.querySelector(".addlapangan-close"); // Tombol tutup Add Field
const addFieldForm = document.getElementById("addFieldForm");

const editFieldButtons = document.querySelectorAll(".admin-edit-button");
const editPopup = document.getElementById("editFieldPopup");
const editCloseButton = document.querySelector(".editlapangan-close"); // Tombol tutup Edit Field
const editFieldForm = document.getElementById("editFieldForm");

// Mendapatkan elemen-elemen Modal
const deleteFieldButtons = document.querySelectorAll(".admin-delete-button");
const deleteConfirmationModal = document.getElementById(
  "deleteConfirmationModal"
);
const deleteModalMessage = document.getElementById("delete-modal-message");
const cancelDeleteButton = document.getElementById("cancel-delete");
const confirmDeleteButton = document.getElementById("confirm-delete");

// Variabel untuk menyimpan ID lapangan yang akan dihapus
let lapanganIdToDelete = null;

// Fungsi untuk membuka modal dengan pesan yang sesuai
function openDeleteModal(namaLapangan, id) {
  console.log("Modal dibuka untuk lapangan:", namaLapangan, "dengan ID:", id); // Debugging
  deleteModalMessage.textContent = `Apakah Anda yakin ingin menghapus data "${namaLapangan}"?`;
  deleteConfirmationModal.classList.remove("hidden");
  deleteConfirmationModal.style.display = "flex";
  lapanganIdToDelete = id;
}

// Fungsi untuk menutup modal
function closeDeleteModal() {
  deleteConfirmationModal.classList.add("hidden");
  deleteConfirmationModal.style.display = "none";
  lapanganIdToDelete = null;
}

// Menambahkan event listener untuk tombol "Delete"
deleteFieldButtons.forEach((button) => {
  button.addEventListener("click", (event) => {
    event.preventDefault();
    const id = button.getAttribute("data-id");
    const namaLapangan = button.getAttribute("data-nama");
    console.log(
      "Tombol delete diklik untuk lapangan:",
      namaLapangan,
      "dengan ID:",
      id
    ); // Debugging
    openDeleteModal(namaLapangan, id);
  });
});

// Menutup modal ketika tombol cancel diklik
cancelDeleteButton.addEventListener("click", closeDeleteModal);

// Mengonfirmasi penghapusan ketika tombol confirm diklik
confirmDeleteButton.addEventListener("click", () => {
  if (lapanganIdToDelete) {
    // Mendapatkan token CSRF
    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeader = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");

    // Mengirim permintaan DELETE untuk menghapus lapangan
    fetch(`/admin/lapangan/delete/${lapanganIdToDelete}`, {
      method: "POST",
      headers: {
        [csrfHeader]: csrfToken, // Sertakan token CSRF
        "Content-Type": "application/json",
      },
      body: JSON.stringify({}), // Body kosong jika tidak diperlukan
    })
      .then((response) => {
        if (response.ok) {
          closeDeleteModal();
          window.location.reload();
        } else {
          return response.text().then((text) => {
            throw new Error(text);
          });
        }
      })
      .catch((error) => {
        console.error("Error deleting lapangan:", error);
      });
  }
});

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

// Menampilkan popup Add Field
addFieldButton.addEventListener("click", () => {
  addPopup.classList.add("show");
  addPopup.classList.remove("hidden");
});

// Menutup popup Add Field
addCloseButton.addEventListener("click", () => {
  addPopup.classList.remove("show");
  setTimeout(() => {
    addPopup.classList.add("hidden");
  }, 300);
});

// Menangani pengiriman formulir Add Field
addFieldForm.addEventListener("submit", (event) => {
  event.preventDefault();

  // Mendapatkan nilai dari formulir
  const formData = new FormData(addFieldForm);

  // Mendapatkan token CSRF
  const { csrfToken, csrfHeader } = getCsrfToken();

  // Mengirim data ke server
  fetch("/admin/lapangan/add", {
    method: "POST",
    headers: {
      [csrfHeader]: csrfToken, // Sertakan token CSRF
    },
    body: formData,
  })
    .then((response) => {
      if (response.ok) {
        // Menutup popup dan me-refresh tabel
        addPopup.classList.remove("show");
        setTimeout(() => {
          addPopup.classList.add("hidden");
          location.reload();
        }, 300);
      } else {
        response.text().then((text) => {
          alert("Gagal menambahkan lapangan: " + text);
        });
      }
    })
    .catch((error) => {
      alert("Terjadi kesalahan.");
    });
});

// Menutup popup Add Field saat klik di luar konten popup
addPopup.addEventListener("click", (event) => {
  if (event.target === addPopup) {
    console.log("Clicked outside the add popup content");
    addPopup.classList.remove("show");
    setTimeout(() => {
      addPopup.classList.add("hidden");
    }, 300);
  }
});

// Menampilkan popup Edit Field dengan data yang ada
editFieldButtons.forEach((button) => {
  button.addEventListener("click", (event) => {
    event.preventDefault(); // Mencegah navigasi default

    const id = button.getAttribute("data-id");
    console.log("Lapangan ID yang dikirim:", id); // Logging untuk verifikasi

    if (!id) {
      alert("ID lapangan tidak ditemukan.");
      return;
    }

    fetch(`/admin/lapangan/${id}`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Error fetching data: ${response.statusText}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log("Data Lapangan yang diterima:", data); // Logging data
        document.getElementById("edit-field-id").value = data.id;
        document.getElementById("edit-field-name").value = data.namaLapangan;
        document.getElementById("edit-field-location").value = data.city;
        document.getElementById("edit-field-type").value = data.cabangOlahraga;
        document.getElementById("edit-field-address").value =
          data.alamatLapangan;
        document.getElementById("edit-field-price").value = data.price;
        document.getElementById("edit-field-rating").value = data.rating;
        document.getElementById("edit-field-reviews").value = data.reviews;
        document.getElementById("edit-jamBuka").value = data.jamBuka;
        document.getElementById("edit-jamTutup").value = data.jamTutup;

        // Set facilities
        if (data.facilities) {
          const facilities = data.facilities.split(", ");
          document
            .querySelectorAll('input[name="fieldFacilities"]')
            .forEach((checkbox) => {
              checkbox.checked = facilities.includes(checkbox.value);
            });
        }

        editPopup.classList.add("show");
        editPopup.classList.remove("hidden");
      })
      .catch((error) => {
        console.error("Error fetching lapangan data:", error);
        alert("Gagal mengambil data lapangan.");
      });
  });
});

// Menutup popup Edit Field
editCloseButton.addEventListener("click", () => {
  editPopup.classList.remove("show");
  setTimeout(() => {
    editPopup.classList.add("hidden");
  }, 300);
});

// Menutup popup Edit Field saat klik di luar konten popup
editPopup.addEventListener("click", (event) => {
  if (event.target === editPopup) {
    console.log("Clicked outside the edit popup content");
    editPopup.classList.remove("show");
    setTimeout(() => {
      editPopup.classList.add("hidden");
    }, 300);
  }
});

// Menangani pengiriman formulir Edit Field
editFieldForm.addEventListener("submit", (event) => {
  event.preventDefault();

  // Mendapatkan nilai dari formulir
  const formData = new FormData(editFieldForm);

  // Validasi input numerik
  const price = parseInt(formData.get("price"), 10);
  const rating = parseFloat(formData.get("rating"));
  const reviews = parseInt(formData.get("reviews"), 10);

  if (isNaN(price) || isNaN(rating) || isNaN(reviews)) {
    alert("Harga, Rating, dan Reviews harus berupa angka.");
    return;
  }

  if (rating < 0 || rating > 5) {
    alert("Rating harus antara 0 dan 5.");
    return;
  }

  // Mendapatkan token CSRF
  const { csrfToken, csrfHeader } = getCsrfToken();

  // Mengirim data ke server
  fetch("/admin/lapangan/edit", {
    method: "POST",
    headers: {
      [csrfHeader]: csrfToken, // Sertakan token CSRF
    },
    body: formData,
  })
    .then((response) => {
      if (response.ok) {
        // Menutup popup dan me-refresh tabel
        editPopup.classList.remove("show");
        setTimeout(() => {
          editPopup.classList.add("hidden");
          location.reload();
        }, 300);
      } else {
        response.text().then((text) => {
          alert("Gagal mengedit lapangan: " + text);
        });
      }
    })
    .catch((error) => {
      alert("Terjadi kesalahan.");
      console.error("Error editing lapangan:", error);
    });
});
