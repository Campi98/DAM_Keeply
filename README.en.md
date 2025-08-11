# Keeply (Android App) → [PT Version](README.md)

Mobile app for simple note management with offline support, backend synchronization, and image attachments.

## Main Features

- User registration and authentication.
- Create, edit, and delete notes.
- Support for images captured with CameraX or chosen from the gallery.
- Local storage with Room (offline operation).
- Synchronization with backend (Retrofit + REST API).
- Soft-deletion with local preservation until synchronization.
- Visual indication of created/edited notes via timestamp and sync state.

## Technologies and Libraries

(See exact versions in `gradle/libs.versions.toml` or `app/build.gradle.kts`)

- Kotlin + Coroutines / Flow
- AndroidX AppCompat, Core KTX, Activity, ConstraintLayout
- Material Design Components
- Room (Local persistence)
- Retrofit + Gson Converter (Networking)
- CameraX (Image capture)

## Project Structure

```
app/
  src/main/java/pt/ipt/dam/a25269a24639/keeply/
    activity/        (Login, Register, Main, NoteDetail, Camera, FullscreenImage)
    api/             (Retrofit interfaces: UserApi, NoteApi)
    data/
      domain/        (Room entities: User, Note)
      dto/           (Request/response DTOs)
      infrastructure/
        Note/        (NoteDao, NoteDatabase, NoteRepository)
        User/        (UserDao, UserRepository)
    util/            (ImageUtils, etc.)
  res/               (Layouts, drawables, menus, strings, themes)
imgReadMe/           (Screenshots for documentation)
```

## Persistence and Synchronization

- Each note includes metadata: `timestamp`, `synced`, and `isDeleted`.
- Deletion is logical (flag `isDeleted`) to allow reconciliation with the server.
- Current synchronization:
  - Fetches remote notes and IDs of deleted notes.
  - Marks locally the notes detected as deleted on the server.
  - Resolves conflicts simply by the highest `timestamp`.
  - Offline creations/edits are sent later.

## Screenshots

### Login

<p align="center">
  <img src="imgReadMe/1 - Login.png" alt="Login screen" width="420" />
  
</p>

### Main Screen

<p align="center">
  <img src="imgReadMe/2 - Ecrã Principal.png" alt="Main screen (notes list)" width="420" />
  
</p>

### Edit Note

<p align="center">
  <img src="imgReadMe/3 - Editar Nota.png" alt="Edit Note screen" width="420" />
  
</p>

## Backend

The backend used is available at:
https://github.com/Campi98/Keeply_Backend

Main endpoints:

- `POST /api/users` - Create user
- `POST /api/users/login` - Login
- `POST /api/users/logout` - Logout
- `DELETE /api/users/{id}` - Delete user
- `GET /api/notes?userId=` - List notes
- `POST /api/notes` - Create note
- `PUT /api/notes/{id}` - Update note
- `DELETE /api/notes/{id}` - Mark note as deleted / delete
