    package com.ikhut.messengerapp.presentation

    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import com.ikhut.messengerapp.databinding.ActivityMainBinding
    import com.ikhut.messengerapp.presentation.authentification.LoginFragment

    class MainActivity : AppCompatActivity() {
        private var _binding: ActivityMainBinding? = null
        private val binding get() = _binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            _binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainActivityFragmentContainerView.id, LoginFragment())
                    .commit()
            }
        }
    }