package fr.enssat.babelblock.jourdren_duchene

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// parent activity to manage menuInflater on each activities
open class BaseActivity: AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // get inflater menu
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        // add inflater menu to the activities
        return super.onCreateOptionsMenu(menu)
    }

    // manage menu inflater click and open the relative activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            // Home
            R.id.main_menu_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                return true
            }

            // Text to speech
            R.id.main_menu_tts -> {
                val intent = Intent(this, TextToSpeechActivity::class.java)
                startActivity(intent)
                return true
            }

            // Translation
            R.id.main_menu_translation -> {
                val intent = Intent(this, TranslatorActivity::class.java)
                startActivity(intent)
                return true
            }

            // Speech to text
            R.id.main_menu_stt -> {
                val intent = Intent(this, SpeechToTextActivity::class.java)
                startActivity(intent)
                return true
            }

            // Block demo
            R.id.main_menu_block -> {
                val intent = Intent(this, BlockActivity::class.java)
                startActivity(intent)
                return true
            }

            // Final translation pipelining
            R.id.main_menu_pipeline -> {
                Toast.makeText(this, "TODO", Toast.LENGTH_LONG).show()
                return true
            }

            // otherwise we bring the selection request to the parent
            else -> super.onOptionsItemSelected(item)
        }
    }
}