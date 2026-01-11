# LojaSocial

An Android application for managing a social store system, designed to help organizations manage beneficiaries, families, products, and distribution schedules.

## Overview

LojaSocial is a mobile application built for Android that facilitates the management of a social store ("Loja Social" in Portuguese). The app provides tools for administrators to manage inventory, track beneficiaries and their families, schedule distributions, and handle product scanning through barcode integration.

## Features

### User Management
- User authentication and registration system
- Role-based access (Admin and regular users)
- Profile management

### Product Management
- Create, view, and edit products
- Barcode scanning for quick product identification
- Product inventory tracking

### Beneficiary Management
- Register and manage beneficiaries
- Track beneficiary information and status
- View detailed beneficiary profiles

### Family Management
- Create and manage family units
- Link beneficiaries to families
- Track family-level information

### Agenda System
- Schedule distribution events
- Manage appointment calendars
- Track scheduled activities

### Shopping Cart
- Cart management for product distribution
- Track items allocated to beneficiaries

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Dagger Hilt

### Firebase Integration
- Firebase Authentication
- Firebase Firestore (Database)
- Firebase Analytics

### Key Libraries
- **CameraX**: Camera functionality for barcode scanning
- **ML Kit**: Barcode scanning capabilities
- **Coil**: Image loading and caching
- **OkHttp**: HTTP client
- **Material Design 3**: Modern UI components

## Requirements

- **Minimum SDK**: Android 7.0 (API level 24)
- **Target SDK**: Android 14 (API level 36)
- **Compile SDK**: Android 14 (API level 36)

### Permissions
- Camera access (for barcode scanning)
- Storage access (read/write external storage)

## Project Structure

```
app/src/main/java/com/example/lojasocial/
├── models/              # Data models and repositories
│   ├── Product.kt
│   ├── Beneficiary.kt
│   ├── Family.kt
│   ├── Agenda.kt
│   ├── Cart.kt
│   └── User.kt
├── ui/                  # UI components and screens
│   ├── login/          # Login screen
│   ├── register/       # Registration screen
│   ├── admin/          # Admin dashboard
│   ├── product/        # Product management
│   ├── Beneficiary/    # Beneficiary management
│   ├── family/         # Family management
│   ├── agendas/        # Agenda management
│   ├── carts/          # Shopping cart
│   ├── perfil/         # User profile
│   └── BarcodeScanner/ # Barcode scanning
├── MainActivity.kt      # Main activity with navigation
├── AppModule.kt        # Dependency injection module
└── Constants.kt        # App constants
```

## Getting Started

### Prerequisites
1. Android Studio (latest version recommended)
2. JDK 11 or higher
3. Firebase project setup with:
   - Authentication enabled
   - Firestore database configured
   - `google-services.json` file placed in the `app/` directory

### Building the Project

1. Clone the repository:
```bash
git clone <repository-url>
cd LojaSocial
```

2. Open the project in Android Studio

3. Add your `google-services.json` file to the `app/` directory

4. Sync Gradle files

5. Build and run the project on an emulator or physical device

### Configuration

Ensure you have a Firebase project configured with:
- Authentication methods enabled (Email/Password)
- Firestore database with appropriate security rules
- Required Firebase services activated

## Development

### Build Configuration
- Uses Kotlin DSL for Gradle configuration
- Hilt for dependency injection with KSP (Kotlin Symbol Processing)
- Compose for UI development

### Testing
- Unit tests located in `app/src/test/`
- Instrumented tests in `app/src/androidTest/`

## License

This project is part of a school assignment (Escola 3º ano).

## Contributing

This is an educational project. If you're a team member, please follow the existing code structure and patterns.

## Support

For issues or questions, please contact the development team or create an issue in the project repository.
