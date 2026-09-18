package com.example.kayara

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BrowseOrgsFragment : Fragment() {

    private lateinit var orgAdapter: BrowseOrgAdapter
    private lateinit var chipAll: TextView
    private lateinit var chipJoined: TextView
    private lateinit var chipPending: TextView

    // TODO: I-replace ni sa Firestore query (organizations collection + memberships status)
    private val organizations = listOf(
        OrganizationModel("Red Cross Youth", "RCY", 98, "Ma'am Reyes", "joined", "#12B5A6", "#0C8E82"),
        OrganizationModel("IT Society", "ITS", 76, "Sir Torres", "joined", "#2E9DF7", "#4FC3F7"),
        OrganizationModel("Supreme Student Government", "SSG", 212, "Ma'am Lopez", "pending", "#0B2E6B", "#2A4E92"),
        OrganizationModel("Educators' Society", "EDS", 140, "Sir Bautista", "available", "#F5A524", "#E08A0F"),
        OrganizationModel(
            "Primes Energy", "PE", 28, "Ma'am Cruz", "available", "#9C27B0", "#6A1B9A",
            recruitmentInfo = "Audition required. Please prepare a 1-minute dance routine and message the adviser to schedule your audition."
        ),
        OrganizationModel(
            "BISU Chorale", "BC", 19, "Sir Ramos", "available", "#00838F", "#006064",
            recruitmentInfo = "Voice audition required. Please prepare a short vocal piece of your choice."
        ),
        OrganizationModel("BS Agri-Forestry Society", "BAF", 65, "Sir Dela Cruz", "available", "#EF5350", "#C63A37")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_browse_orgs, container, false)

        val rvBrowseOrgs = view.findViewById<RecyclerView>(R.id.rvBrowseOrgs)
        val etSearch = view.findViewById<EditText>(R.id.etSearchOrgStudent)
        chipAll = view.findViewById(R.id.chipAllStudentOrgs)
        chipJoined = view.findViewById(R.id.chipJoined)
        chipPending = view.findViewById(R.id.chipPendingOrgs)

        rvBrowseOrgs.layoutManager = LinearLayoutManager(requireContext())
        orgAdapter = BrowseOrgAdapter(organizations) { org ->
            showJoinDialog(org)
        }
        rvBrowseOrgs.adapter = orgAdapter

        val joinedCount = organizations.count { it.category == "joined" }
        val pendingCount = organizations.count { it.category == "pending" }
        chipAll.text = "All (${organizations.size})"
        chipJoined.text = "Joined ($joinedCount)"
        chipPending.text = "Pending ($pendingCount)"

        chipAll.setOnClickListener {
            setActiveChip(chipAll)
            orgAdapter.filter("all")
        }
        chipJoined.setOnClickListener {
            setActiveChip(chipJoined)
            orgAdapter.filter("joined")
        }
        chipPending.setOnClickListener {
            setActiveChip(chipPending)
            orgAdapter.filter("pending")
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                orgAdapter.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun setActiveChip(activeChip: TextView) {
        val allChips = listOf(chipAll, chipJoined, chipPending)
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

    private fun showJoinDialog(org: OrganizationModel) {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_join_org, null)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.setGravity(android.view.Gravity.BOTTOM)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvJoinDialogTitle)
        val recruitmentBanner = dialogView.findViewById<LinearLayout>(R.id.recruitmentInfoBanner)
        val tvRecruitmentText = dialogView.findViewById<TextView>(R.id.tvRecruitmentInfoText)
        val etJoinMessage = dialogView.findViewById<EditText>(R.id.etJoinMessage)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancelJoin)
        val btnSubmit = dialogView.findViewById<TextView>(R.id.btnSubmitJoin)

        tvTitle.text = "Join ${org.orgName}"

        if (org.recruitmentInfo.isNotBlank()) {
            recruitmentBanner.visibility = View.VISIBLE
            tvRecruitmentText.text = org.recruitmentInfo
        } else {
            recruitmentBanner.visibility = View.GONE
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSubmit.setOnClickListener {
            val message = etJoinMessage.text.toString().trim()

            // TODO: I-save sa Firestore memberships collection:
            //   { studentId: myUserId, organizationId: org.id,
            //     status: "pending", applicationNote: message, dateApplied: timestamp }
            // Awtomatiko na makita sa Admin's Members tab isip Pending Applicant.

            Toast.makeText(
                requireContext(),
                "Request to join ${org.orgName} sent!",
                Toast.LENGTH_LONG
            ).show()

            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}