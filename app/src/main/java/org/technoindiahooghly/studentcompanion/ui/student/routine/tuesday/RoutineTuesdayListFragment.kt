package org.technoindiahooghly.studentcompanion.ui.student.routine.tuesday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.technoindiahooghly.studentcompanion.R
import org.technoindiahooghly.studentcompanion.StudentApplication
import org.technoindiahooghly.studentcompanion.adapter.student.routine.tuesday.RoutineTuesdayListAdapter
import org.technoindiahooghly.studentcompanion.alarm.student.alarmHandler
import org.technoindiahooghly.studentcompanion.data.student.TuesdayData
import org.technoindiahooghly.studentcompanion.databinding.FragmentRoutineTuesdayListBinding
import org.technoindiahooghly.studentcompanion.ui.student.routine.RoutineViewPagerDirections
import org.technoindiahooghly.studentcompanion.viewmodel.student.StudentViewModel
import org.technoindiahooghly.studentcompanion.viewmodel.student.StudentViewModelFactory

class RoutineTuesdayListFragment :
    Fragment(),
    RoutineTuesdayListAdapter.TuesdayDeleteClickInterface,
    RoutineTuesdayListAdapter.TuesdayNotifyClickInterface {
    private val viewModel: StudentViewModel by activityViewModels {
        StudentViewModelFactory(
            (activity?.application as StudentApplication).studentDatabase.studentDao())
    }

    private var _binding: FragmentRoutineTuesdayListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineTuesdayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            RoutineTuesdayListAdapter(this, this) {
                val action =
                    RoutineViewPagerDirections
                        .actionRoutineViewPager2ToRoutineTuesdayAddUpdateFragment(it.id)
                this.findNavController().navigate(action)
            }

        binding.tuesdayListRecyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.tuesdayListRecyclerView.adapter = adapter

        viewModel.getTuesday.observe(this.viewLifecycleOwner) { it.let { adapter.submitList(it) } }

        binding.addTuesdayRoutineFAB.setOnClickListener {
            val action =
                RoutineViewPagerDirections
                    .actionRoutineViewPager2ToRoutineTuesdayAddUpdateFragment()
            this.findNavController().navigate(action)
        }
    }

    override fun onDeleteImageViewClick(entry: TuesdayData) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setPositiveButton(requireContext().getString(R.string.yes_alert_dialog)) { _, _ ->
            alarmHandler(
                requireContext(), entry.id, entry.subjectName, entry.tuesdayStartTime, CANCEL_ALARM)

            viewModel.setNotify(CANCEL_ALARM, entry.id, DAY)
            viewModel.deleteEntry(entry.id, DAY)

            Toast.makeText(
                    requireContext(),
                    requireContext()
                        .getString(R.string.class_remove_success_toast, entry.subjectName, DAY),
                    Toast.LENGTH_SHORT)
                .show()
        }

        builder.setNegativeButton(requireContext().getString(R.string.no_alert_dialog)) { _, _ -> }
        builder.setTitle(
            requireContext().getString(R.string.class_remove_prompt_title, entry.subjectName, DAY))
        builder.setMessage(requireContext().getString(R.string.class_remove_prompt_message, DAY))
        builder.create().show()
    }

    override fun onNotifyImageViewClick(entry: TuesdayData) {
        if (!entry.tuesdayNotification) {
            alarmHandler(
                requireContext(), entry.id, entry.subjectName, entry.tuesdayStartTime, SET_ALARM)

            viewModel.setNotify(SET_ALARM, entry.id, DAY)

            Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.class_notify_on, entry.subjectName, DAY),
                    Toast.LENGTH_SHORT)
                .show()
        } else {
            alarmHandler(
                requireContext(), entry.id, entry.subjectName, entry.tuesdayStartTime, CANCEL_ALARM)

            viewModel.setNotify(CANCEL_ALARM, entry.id, DAY)

            Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.class_notify_off, entry.subjectName, DAY),
                    Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val DAY = "Tuesday"
        private const val SET_ALARM = true
        private const val CANCEL_ALARM = !SET_ALARM
    }
}