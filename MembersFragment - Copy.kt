package com.example.kayara

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MembersFragment : Fragment() {

    private lateinit var memberAdapter: MemberAdapter
    private lateinit var chipAll: TextView
    private lateinit var chipOfficers: TextView
    private lateinit var chipPending: TextView

    private val members = mutableListOf(
        MemberModel("Angela Torres", "Applied 2 days ago", "pending", "#F5A524", "#E08A0F"),
        MemberModel("Jomar Ramos", "Applied 2 days ago", "pending", "#F5A524", "#E08A0F"),
        MemberModel("Juliana Dizon", "BSN-3A - Secretary", "officer", "#2E9DF7", "#4FC3F7"),
        MemberModel("Krystal Lim", "BSIT-1A - Member", "member", "#0B2E6B", "#2A4E92")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_members, container, false)

        val rvMembers = view.findViewById<RecyclerView>(R.id.rvMembers)
        val etSearchMember = view.findViewById<EditText>(R.id.etSearchMember)
        chipAll = view.findViewById(R.id.chipAllMembers)
        chipOfficers = view.findViewById(R.id.chipOfficers)
        chipPending = view.findViewById(R.id.chipPendingMembers)

        rvMembers.layoutManager = LinearLayoutManager(requireContext())
        memberAdapter = MemberAdapter(
            members,
            onApprove = { position ->
                val member = members[position]
                Toast.makeText(requireContext(), "${member.fullName} approved as member!", Toast.LENGTH_SHORT).show()
                memberAdapter.updateRoleAt(position, "member")
                updateChipCounts()
            },
            onReject = { position ->
                val member = members[position]
                Toast.makeText(requireContext(), "${member.fullName}'s application rejected", Toast.LENGTH_SHORT).show()
                memberAdapter.removeItemAt(position)
                updateChipCounts()
            }
        )
        rvMembers.adapter = memberAdapter

        updateChipCounts()

        chipAll.setOnClickListener {
            setActiveChip(chipAll)
            memberAdapter.filter("all")
        }
        chipOfficers.setOnClickListener {
            setActiveChip(chipOfficers)
            memberAdapter.filter("officer")
        }
        chipPending.setOnClickListener {
            setActiveChip(chipPending)
            memberAdapter.filter("pending")
        }

        etSearchMember.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                memberAdapter.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun setActiveChip(activeChip: TextView) {
        val allChips = listOf(chipAll, chipOfficers, chipPending)
        allChips.forEach { chip ->
            if (chip == activeChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_active)
                chip.setTextColor(resources.getColor(R.color.white, null))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip)
                chip.setTextColor(resources.getColor(R.color.text_gray, null))
            }
        }
    }

    private fun updateChipCounts() {
        chipAll.text = "All (${members.size})"
        chipOfficers.text = "Officers (${members.count { it.role == "officer" }})"
        chipPending.text = "Pending (${members.count { it.role == "pending" }})"
    }
}