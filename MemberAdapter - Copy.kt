package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(
    private var fullList: MutableList<MemberModel>,
    private val onApprove: (Int) -> Unit,
    private val onReject: (Int) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private var displayedList: MutableList<MemberModel> = fullList

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvMemberAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvMemberName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvMemberDetails)
        val tvRoleTag: TextView = itemView.findViewById(R.id.tvMemberRoleTag)
        val btnApprove: ImageView = itemView.findViewById(R.id.btnApproveMember)
        val btnReject: ImageView = itemView.findViewById(R.id.btnRejectMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = displayedList[position]
        holder.tvAvatar.text = getInitials(member.fullName)
        holder.tvAvatar.background.setTint(android.graphics.Color.parseColor(member.avatarColorStart))
        holder.tvName.text = member.fullName
        holder.tvDetails.text = member.details

        val tvNote = holder.itemView.findViewById<TextView>(R.id.tvApplicationNote)
        if (member.applicationNote.isNotBlank() && member.role == "pending") {
            tvNote.visibility = View.VISIBLE
            tvNote.text = "\uD83D\uDCAC \"${member.applicationNote}\""
        } else {
            tvNote.visibility = View.GONE
        }

        when (member.role) {
            "officer" -> {
                holder.tvRoleTag.text = "OFFICER"
                holder.tvRoleTag.setBackgroundResource(R.drawable.bg_role_officer)
                holder.tvRoleTag.setTextColor(holder.itemView.context.getColor(R.color.blue_deep))
                holder.btnApprove.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
            "pending" -> {
                holder.tvRoleTag.text = "PENDING"
                holder.tvRoleTag.setBackgroundResource(R.drawable.bg_tag_pending)
                holder.tvRoleTag.setTextColor(holder.itemView.context.getColor(R.color.amber_text))
                holder.btnApprove.visibility = View.VISIBLE
                holder.btnReject.visibility = View.VISIBLE
            }
            else -> {
                holder.tvRoleTag.text = "MEMBER"
                holder.tvRoleTag.setBackgroundResource(R.drawable.bg_role_member)
                holder.tvRoleTag.setTextColor(holder.itemView.context.getColor(R.color.text_gray))
                holder.btnApprove.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
        }

        holder.btnApprove.setOnClickListener { onApprove(holder.adapterPosition) }
        holder.btnReject.setOnClickListener { onReject(holder.adapterPosition) }
    }

    override fun getItemCount(): Int = displayedList.size

    fun filter(role: String) {
        displayedList = when (role) {
            "officer" -> fullList.filter { it.role == "officer" }.toMutableList()
            "pending" -> fullList.filter { it.role == "pending" }.toMutableList()
            else -> fullList
        }
        notifyDataSetChanged()
    }

    fun search(query: String) {
        displayedList = if (query.isBlank()) {
            fullList
        } else {
            fullList.filter { it.fullName.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        val removedMember = displayedList[position]
        fullList.remove(removedMember)
        displayedList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateRoleAt(position: Int, newRole: String) {
        displayedList[position] = displayedList[position].copy(role = newRole)
        notifyItemChanged(position)
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else name.take(2).uppercase()
    }
}