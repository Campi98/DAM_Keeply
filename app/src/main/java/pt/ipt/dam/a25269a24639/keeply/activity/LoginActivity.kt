package pt.ipt.dam.a25269a24639.keeply.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.api.LoginRequest
import pt.ipt.dam.a25269a24639.keeply.api.UserApi
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.User.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Activity responsável pela autenticação do utilizador.
 *
 * Esta Activity gere:
 * - Login do utilizador
 * - Registo de novos utilizadores
 * - Apresentação de diálogos informativos
 * - Gestão do estado de autenticação
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    // Configuração do Retrofit para comunicação com a API
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://keeplybackend-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(UserApi::class.java)


    /**
     * Launcher para gerir o resultado do registo de utilizador
     * Preenche automaticamente os campos de email e password após registo bem-sucedido
     */
    private val registerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val email = data.getStringExtra("email") ?: ""
                val password = data.getStringExtra("password") ?: ""
                emailInput.setText(email)
                passwordInput.setText(password)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Checka se é a primeira vez que o utilizador abre a aplicação
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            showConsentDialog()
            // Tornar isFirstLaunch falso para não mostrar o consentimento novamente
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
        }

        // PARA DEBUG: reset ao first launch com long click no botão de login
        findViewById<Button>(R.id.loginButton).setOnLongClickListener {
            resetFirstLaunch()
            Toast.makeText(this, "First launch reset!", Toast.LENGTH_LONG).show()
            true
        }

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val aboutButton = findViewById<Button>(R.id.aboutButton)

        // Inicializa o banco de dados e repositório
        val database = NoteDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())

        //Verificar o estado de login na inicialização
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            goToMainActivity()
        }

        // Lógica do botão de login
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateFields(email, password)) {
                login(email, password)
            }
        }

        aboutButton.setOnClickListener {
            showAboutDialog()
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            registerLauncher.launch(intent)
        }


        // Diálogo com informações sobre as bibliotecas utilizadas
        findViewById<Button>(R.id.librariesButton).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Bibliotecas Utilizadas")
                .setMessage(
                    """
                Core Libraries:
                • AndroidX Core
                • AndroidX AppCompat 
                • AndroidX Activity
                • AndroidX ConstraintLayout

                UI Components:
                • Material Design Components
                • RecyclerView
                • CardView 

                Database:
                • Room Persistence Library (v2.6.1)

                Camera:
                • CameraX (v1.3.2)

                Networking:
                • Retrofit 2 (v2.9.0)
            """.trimIndent()
                )
                .setPositiveButton("OK", null)
                .show()
        }
    }

    /**
     * Mostra um diálogo com informações sobre a aplicação.
     */
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sobre nós")
            .setMessage("Instituto Politécnico de Tomar\n\nLicenciatura em Engenharia Informática\nDesenvolvimento de Aplicações Móveis\n\n 2024/2025\n" +
                    "\n" +
                    "João Campos nº 25269 \n" +
                    "Cristiane Mayabanza nº 24639")
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Mostra um diálogo de consentimento ao utilizador.
     * - O utilizador pode aceitar ou recusar a política de privacidade.
     * - Se recusar, a aplicação é fechada.
     */
    private fun showConsentDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Bem-vindo ao Keeply!")
            .setMessage(
                "O Keeply é uma aplicação simples e prática para tirar notas.\n\n" +
                        "Ao utilizar esta aplicação, concorda com:\n\n" +
                        "• Armazenamento local das suas notas\n" +
                        "• Armazenamento \"seguro\" das suas credenciais\n" +
                        "• Utilização de dados apenas para funcionamento da app\n\n" +
                        "Não partilhamos os seus dados com terceiros."
            )
            .setCancelable(false)
            .setPositiveButton("Aceitar") { _, _ -> // Nada, user aceitou
            }
            .setNegativeButton("Recusar") { _, _ ->
                resetFirstLaunch()
                finish()        // User não aceitou, fechar a aplicação
            }
            .show()
    }


    /**
     * Dá reset ao estado do first launch para debug (para mostrar a privacy policy outra vez).
     */
    private fun resetFirstLaunch() {
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
            .edit()
            .putBoolean("isFirstLaunch", true)
            .apply()
    }

    /**
     * Valida os campos de email e senha.
     * - Exibe mensagens ao usuário se os campos estiverem vazios.
     *
     * @param email O email inserido pelo usuário.
     * @param password A palavra passe inserida pelo usuário.
     * @return True se os campos forem válidos, False caso contrário.
     */
    private fun validateFields(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira o email.", Toast.LENGTH_SHORT).show()
                false
            }

            password.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira a senha.", Toast.LENGTH_SHORT).show()
                false
            }

            else -> true
        }
    }

    /**
     * Realiza o login ao verificar as credenciais no banco de dados Room.
     * - Caso o login seja bem-sucedido, salva o estado de login e redireciona para a MainActivity.
     * - Caso contrário, exibe uma mensagem de erro.
     *
     * @param email O email inserido pelo usuário.
     * @param password A palavra passe inserida pelo usuário.
     */
    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val user = response.body()!!
                saveLoginState(user.loggedIn, email, user.userId)
                Toast.makeText(this@LoginActivity, "Bem-vindo, ${user.name}!", Toast.LENGTH_SHORT)
                    .show()
                goToMainActivity()
            } else {
                Toast.makeText(this@LoginActivity, "Email ou senha inválidos!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Salva o estado de login no SharedPreferences.
     */
    private fun saveLoginState(isLoggedIn: Boolean, email: String, userId: Int) {
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
            .putBoolean("isLoggedIn", isLoggedIn)
            .putString("email", email)
            .putInt("userId", userId)
            .apply()
    }

    /**
     * Redireciona o usuário para a MainActivity.
     */
    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
