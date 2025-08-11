# Keeply (Aplicação Android) → [EN Version](README.en.md)

Aplicação móvel para gestão simples de notas com suporte a funcionamento offline, sincronização com backend remoto e anexação de imagens.

## Funcionalidades Principais

- Registo e autenticação de utilizadores.
- Criação, edição e eliminação de notas.
- Suporte a imagens capturadas pela câmara (CameraX) ou escolhidas da galeria.
- Armazenamento local com Room (funcionamento offline).
- Sincronização com backend (Retrofit + REST API).
- Marcação de notas eliminadas com preservação local até sincronização.
- Indicação visual de notas criadas/editadas através do timestamp e estado de sincronização.

## Tecnologias e Bibliotecas

(Ver versões exactas em `gradle/libs.versions.toml` ou `app/build.gradle.kts`)

- Kotlin + Coroutines / Flow
- AndroidX AppCompat, Core KTX, Activity, ConstraintLayout
- Material Design Components
- Room (Persistência local)
- Retrofit + Gson Converter (Networking)
- CameraX (Captura de imagens)

## Estrutura do Projecto

```
app/
  src/main/java/pt/ipt/dam/a25269a24639/keeply/
    activity/        (Login, Register, Main, NoteDetail, Camera, FullscreenImage)
    api/             (Interfaces Retrofit: UserApi, NoteApi)
    data/
      domain/        (Entidades Room: User, Note)
      dto/           (DTOs para requests/responses)
      infrastructure/
        Note/        (NoteDao, NoteDatabase, NoteRepository)
        User/        (UserDao, UserRepository)
    util/            (ImageUtils, etc.)
  res/               (Layouts, drawables, menus, strings, themes)
imgReadMe/           (Capturas de ecrã para documentação)
```

## Persistência e Sincronização

- Cada nota inclui metadados: `timestamp`, `synced` e `isDeleted`.
- Eliminação é lógica (flag `isDeleted`) para permitir reconciliação com o servidor.
- Sincronização actual:
  - Obtém notas remotas e IDs de notas eliminadas.
  - Marca localmente notas eliminadas detectadas no servidor.
  - Resolve conflitos simples via maior `timestamp`.
  - Criações/alterações offline são enviadas posteriormente.

## Screenshots

### Login

<p align="center">
  <img src="imgReadMe/1 - Login.png" alt="Ecrã de Login" width="420" />
</p>

### Ecrã Principal

<p align="center">
  <img src="imgReadMe/2 - Ecrã Principal.png" alt="Ecrã Principal (lista de notas)" width="420" />
</p>

### Editar Nota

<p align="center">
  <img src="imgReadMe/3 - Editar Nota.png" alt="Ecrã de Edição de Nota" width="420" />
</p>

## Backend

O backend utilizado encontra-se em:
https://github.com/Campi98/Keeply_Backend

Endpoints principais:

- `POST /api/users` - Criar utilizador
- `POST /api/users/login` - Login
- `POST /api/users/logout` - Logout
- `DELETE /api/users/{id}` - Eliminar utilizador
- `GET /api/notes?userId=` - Listar notas
- `POST /api/notes` - Criar nota
- `PUT /api/notes/{id}` - Actualizar nota
- `DELETE /api/notes/{id}` - Marcar nota como eliminada / eliminar
