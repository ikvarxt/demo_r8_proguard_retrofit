package me.ikvarxt.testr8minifyaar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.ikvarxt.request.getRepos
import me.ikvarxt.request.listOrgMembers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loading = "loading..."
        val text = findViewById<TextView>(R.id.text)

        val action = {
            text.text = loading
            getRepos({ list ->
                text.text = list.joinToString("\n") { it.shortToString() }
            }, {
                text.text = it.toString()
                Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show()
            })
        }
        action()

        val action2 = {
            text.text = loading
            val filters = hashMapOf(
                "role" to "member",
                "per_page" to "35",
            )
            listOrgMembers("vuejs", filters, {
                Log.d("TAG", "list org members: total: ${it.size}")
                text.text = it.joinToString("\n")
            }, {
                text.text = it.toString()
                Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show()
            })
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            action2()
        }
    }
}