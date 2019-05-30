package ba.rubicon.batteryview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chargingButton.setOnClickListener {
            battery_view.isCharging = !battery_view.isCharging
        }
        battery_value.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val value = s.toString().toInt()
                    battery_view.batteryLevel = value
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity,"Invalid battery value", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}
