// JavaScript for image slider
let currentIndex = 0;
const slides = document.querySelectorAll('.slide');
const sliderContainer = document.querySelector('.slider-container');
const navbarHeight = document.querySelector('nav').offsetHeight;

// Function to set the height of slider container
function setSliderContainerHeight() {
    const viewportHeight = window.innerHeight;
    const containerHeight = viewportHeight - navbarHeight;
    sliderContainer.style.height = containerHeight + 'px';
}

// Function to show a specific slide
function showSlide(index) {
    slides.forEach((slide, i) => {
        if (i === index) {
            slide.style.display = 'block';
        } else {
            slide.style.display = 'none';
        }
    });
}

// Function to move to the next slide
function nextSlide() {
    currentIndex = (currentIndex + 1) % slides.length;
    showSlide(currentIndex);
}

// Set interval for automatic slide transition (change slide every 5 seconds)
setInterval(nextSlide, 5000);

// Show the first slide initially
showSlide(currentIndex);

// Set initial height of slider container
setSliderContainerHeight();

// Display the booking success message as a toast notification
const bookingSuccessMessageSpan = document.querySelector("#bookingSuccessMessage span");
if (bookingSuccessMessageSpan) {
    const bookingSuccessMessage = bookingSuccessMessageSpan.textContent;
    if (bookingSuccessMessage) {
        toastr.success(bookingSuccessMessage);
        // Automatically remove the message after 3 seconds
        setTimeout(function() {
            bookingSuccessMessageSpan.parentElement.style.display = "none";
        }, 3000);
    }
}


