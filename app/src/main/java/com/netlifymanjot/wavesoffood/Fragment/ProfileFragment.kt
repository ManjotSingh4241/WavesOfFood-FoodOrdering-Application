package com.netlifymanjot.wavesoffood.Fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.netlifymanjot.wavesoffood.R


class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private lateinit var userRef: DatabaseReference
    private lateinit var nameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize EditText fields
        nameEditText = view.findViewById(R.id.nameEditText)
        addressEditText = view.findViewById(R.id.addressEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            userRef = database.getReference("user").child(userId)
            fetchUserData()
        } else {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.saveInfoButton).setOnClickListener {
            saveUserData()
        }

        return view
    }

    private fun fetchUserData() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: ""
                    val address = snapshot.child("address").value?.toString() ?: ""
                    val email = snapshot.child("email").value?.toString() ?: ""
                    val phone = snapshot.child("phone").value?.toString() ?: ""

                    nameEditText.setText(name)
                    addressEditText.setText(address)
                    emailEditText.setText(email)
                    phoneEditText.setText(phone)
                } else {
                    Toast.makeText(requireContext(), "No user data found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileFragment", "Error fetching data: ${error.message}")
                Toast.makeText(requireContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData() {
        val name = nameEditText.text.toString()
        val address = addressEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()

        if (validateInput(name, email, phone)) {
            val userMap = mapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )

            userRef.updateChildren(userMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInput(name: String, email: String, phone: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                nameEditText.error = "Name is required"
                false
            }
            TextUtils.isEmpty(email) -> {
                emailEditText.error = "Email is required"
                false
            }
            TextUtils.isEmpty(phone) -> {
                phoneEditText.error = "Phone is required"
                false
            }
            else -> true
        }
    }
}
