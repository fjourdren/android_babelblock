package fr.enssat.babelblock.jourdren_duchene

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.main_menu_home ->  {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                return true
            }
            R.id.main_menu_tts -> {
                val intent = Intent(this, TextToSpeechActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.main_menu_translation -> {
                val intent = Intent(this, TranslatorActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.main_menu_stt -> {
                val intent = Intent(this, SpeechToTextActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.main_menu_block -> {
                val intent = Intent(this, BlockActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.main_menu_pipeline -> {
                Toast.makeText(this, "TODO", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}