package com.example.tp_14804_14861_14876.Fragments

import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsMachineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsMachineFragment : Fragment(), View.OnClickListener {

    lateinit var addMachineFragment: AddMachineFragment
    lateinit var createSuperUserFragment: CreateSuperUserFragment
    lateinit var passwordMachineFragment: PasswordMachineFragment
    lateinit var addExpensesFragment: AddExpensesFragment
    lateinit var changeDiagnosticPeriodFragment: ChangeDiagnosticPeriodFragment
    lateinit var transaction: FragmentTransaction

    lateinit var settingsMachine_tv_addMachine: TextView
    lateinit var settingsMachine_tv_addSuperUser: TextView
    lateinit var settingsMachine_tv_addExpenses: TextView
    lateinit var settingsMachine_tv_changeDiagnostic: TextView
    lateinit var settingsMachine_tv_changePassword: TextView

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
        return inflater.inflate(R.layout.fragment_settings_machine, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsMachineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsMachineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsMachine_tv_addMachine = view.findViewById<TextView>(R.id.settingsMachine_tv_addMachine)
        settingsMachine_tv_addSuperUser = view.findViewById<TextView>(R.id.settingsMachine_tv_addSuperUser)
        settingsMachine_tv_addExpenses = view.findViewById<TextView>(R.id.settingsMachine_tv_addExpenses)
        settingsMachine_tv_changeDiagnostic = view.findViewById<TextView>(R.id.settingsMachine_tv_changeDiagnostic)
        settingsMachine_tv_changePassword = view.findViewById<TextView>(R.id.settingsMachine_tv_changePassword)

        settingsMachine_tv_addMachine.setOnClickListener(this)
        settingsMachine_tv_addSuperUser.setOnClickListener(this)
        settingsMachine_tv_addExpenses.setOnClickListener(this)
        settingsMachine_tv_changeDiagnostic.setOnClickListener(this)
        settingsMachine_tv_changePassword.setOnClickListener(this)

//        //Firebase info
//        auth = FirebaseAuth.getInstance()
//        val user = auth?.currentUser
//        val name = user?.displayName
//        val email = user?.email
//        idUser = user?.uid
//        var photo = user?.photoUrl
//        val superUser = user?.email
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingsMachine_tv_addMachine -> {
                addMachineFragment = AddMachineFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, addMachineFragment)
                transaction.commit()
            }
            R.id.settingsMachine_tv_addSuperUser -> {
                createSuperUserFragment = CreateSuperUserFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, createSuperUserFragment)
                transaction.commit()
            }
            R.id.settingsMachine_tv_addExpenses -> {
                addExpensesFragment = AddExpensesFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, addExpensesFragment)
                transaction.commit()
            }
            R.id.settingsMachine_tv_changeDiagnostic -> {
                changeDiagnosticPeriodFragment = ChangeDiagnosticPeriodFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, changeDiagnosticPeriodFragment)
                transaction.commit()
            }
            R.id.settingsMachine_tv_changePassword -> {
                passwordMachineFragment = PasswordMachineFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, passwordMachineFragment)
                transaction.commit()
            }
        }
    }
}