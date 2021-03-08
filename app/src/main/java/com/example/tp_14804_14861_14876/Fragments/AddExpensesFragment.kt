package com.example.tp_14804_14861_14876.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddExpensesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExpensesFragment : Fragment(), View.OnClickListener {

    var misshowpass = false
    var misshowconfirmpass = false
    var validationpassword: String? = "false"

    lateinit var settingsMachineFragment: SettingsMachineFragment
    lateinit var transaction: FragmentTransaction
    lateinit var addExpenses_et_expenses: EditText
    lateinit var addExpenses_et_nameMachine: EditText
    lateinit var addExpenses_et_username: EditText
    lateinit var addExpenses_et_password: EditText
    lateinit var addExpenses_et_confirmpassword: EditText
    lateinit var password_iv_show: ImageView
    lateinit var password_iv_confirmshow: ImageView
    lateinit var addExpenses_btn_add: Button

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_expenses, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddExpensesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddExpensesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addExpenses_et_expenses = view.findViewById<EditText>(R.id.addExpenses_et_expenses)
        addExpenses_et_nameMachine = view.findViewById<EditText>(R.id.addExpenses_et_nameMachine)
        addExpenses_et_username = view.findViewById<EditText>(R.id.addExpenses_et_username)
        addExpenses_et_password = view.findViewById<EditText>(R.id.addExpenses_et_password)
        addExpenses_et_confirmpassword = view.findViewById<EditText>(R.id.addExpenses_et_confirmpassword)
        password_iv_show = view.findViewById<ImageView>(R.id.password_iv_show)
        password_iv_confirmshow = view.findViewById<ImageView>(R.id.password_iv_confirmshow)
        addExpenses_btn_add = view.findViewById<Button>(R.id.addExpenses_btn_add)

        addExpenses_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = addExpenses_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        password_iv_show.setOnClickListener(this)
        password_iv_confirmshow.setOnClickListener(this)
        addExpenses_btn_add.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addExpenses_btn_add-> {
                settingsMachineFragment = SettingsMachineFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, settingsMachineFragment)
                transaction.commit()
            }
            R.id.password_iv_show -> {
                misshowpass = !misshowpass
                showPassword(misshowpass)
            }
            R.id.password_iv_confirmshow -> {
                misshowconfirmpass = !misshowconfirmpass
                showConfirmPassword(misshowconfirmpass)
            }
        }
    }

    private fun showPassword(isShow: Boolean) {
        if (isShow){
            addExpenses_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_hide_password)
        } else {
            addExpenses_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_show_password)
        }
        addExpenses_et_password.setSelection(addExpenses_et_password.text.toString().length)    }

    private fun showConfirmPassword(isShow:Boolean) {
        if (isShow){
            addExpenses_et_confirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            addExpenses_et_confirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_show_password)
        }
        addExpenses_et_confirmpassword.setSelection(addExpenses_et_confirmpassword.text.toString().length)
    }

    private fun validatePassword(password: String): String? {
        val upperCase = Pattern.compile("[A-Z]")
        val lowerCase = Pattern.compile("[a-z]")
        val digitCase = Pattern.compile("[0-9]")
        var validate: String = "false"
        if (lowerCase.matcher(password).find()
            && upperCase.matcher(password).find()
            && digitCase.matcher(password).find()
            && password.length >= 8) {
            validate = "false"
        } else {
            validate = "true"
        }
        return validate
    }
}