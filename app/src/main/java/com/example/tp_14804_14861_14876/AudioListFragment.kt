package com.example.tp_14804_14861_14876

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioListFragment : Fragment(), AudioListAdapter.onItemList_Click {

    lateinit var playerSheet: ConstraintLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var audiolist: RecyclerView

    //var allFiles: Array<File>? = null
    private lateinit var allFiles: Array<File>
    lateinit var timeAgo: TimeAgo
    private var audioListAdapter: AudioListAdapter? = null

    lateinit var mediaPlayer: MediaPlayer
    private var  isPlaying = false
    lateinit var filetoplay: File

    //UI elements
    lateinit var player_btn_play: Button
    lateinit var player_tv_filename: TextView
    lateinit var player_tv_headername: TextView
    lateinit var player_seekbar: SeekBar

    lateinit var seekbarHandler: Handler
    lateinit var updateSeekbar: Runnable

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
        return inflater.inflate(R.layout.fragment_audio_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AudioListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AudioListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerSheet = view.findViewById<ConstraintLayout>(R.id.player_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet)
        audiolist = view.findViewById<RecyclerView>(R.id.audio_list_view)

        player_btn_play = view.findViewById<Button>(R.id.player_btn_play)
        player_tv_filename = view.findViewById<TextView>(R.id.player_tv_filename)
        player_tv_headername = view.findViewById<TextView>(R.id.player_tv_headertitle)
        player_seekbar = view.findViewById<SeekBar>(R.id.player_seekbar)

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val path = requireActivity().getExternalFilesDir("/")!!.absolutePath
        val directory = File(path)
        allFiles = directory.listFiles()

        audioListAdapter = AudioListAdapter(allFiles, this)

        audiolist.setHasFixedSize(true)
        audiolist.setLayoutManager(LinearLayoutManager(context))
        audiolist.setAdapter(audioListAdapter)
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //We cant do anything here for this app
            }
        })

        player_btn_play.setOnClickListener(View.OnClickListener {
            if (isPlaying) {
                pauseAudio()
            } else {
                if (filetoplay != null) {
                    resumeAudio()
                }
            }
        })

        player_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                pauseAudio()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                mediaPlayer.seekTo(progress)
                resumeAudio()
            }
        })
        player_btn_play.isEnabled = false
        player_seekbar.isEnabled = false
    }

    override fun onClickListener(file: File, position: Int) {
        filetoplay = file

        if (isPlaying) {
            pauseAudio()
            //playAudio(filetoplay)
        } else {
            player_btn_play.isEnabled = true
            player_seekbar.isEnabled = true
            playAudio(filetoplay)
        }
    }
    private fun pauseAudio() {
        mediaPlayer.pause()
        player_btn_play.background = requireActivity().resources.getDrawable(
                R.drawable.player_play_btn,
                null
        )
        player_tv_headername.text = "Paused"
        isPlaying = false
        seekbarHandler.removeCallbacks(updateSeekbar)
    }
    private fun resumeAudio() {
        mediaPlayer.start()
        player_btn_play.background = requireActivity().resources.getDrawable(
                R.drawable.player_pause_btn,
                null
        )
        isPlaying = true
        player_tv_headername.text = "Playing"
        updateRunnable()
        seekbarHandler.postDelayed(updateSeekbar, 0)
    }
    private fun stopAudio() {
        //stop the audio
        player_btn_play.background = requireActivity().resources.getDrawable(
                R.drawable.player_play_btn,
                null
        )
        player_tv_headername.text = "Stopped"

        isPlaying = false
        mediaPlayer.stop()
        seekbarHandler.removeCallbacks(updateSeekbar)
    }

    fun playAudio(filetoplay: File) {
        //play the audio
        mediaPlayer = MediaPlayer()

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        try {
            mediaPlayer.setDataSource(filetoplay.toString())
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        player_btn_play.background = requireActivity().resources.getDrawable(
                R.drawable.player_pause_btn,
                null
        )
        player_tv_filename.text = filetoplay.name
        player_tv_headername.text = "Playing"

        isPlaying = true

        mediaPlayer.setOnCompletionListener {
            stopAudio()
            player_tv_headername.text = "Finished"
            mediaPlayer.release()
            player_btn_play.isEnabled = false
            player_seekbar.isEnabled = false
        }

        //duration video
        player_seekbar.max = mediaPlayer.duration

        seekbarHandler = Handler()
        updateRunnable()
        seekbarHandler.postDelayed(updateSeekbar, 0)
    }

    private fun updateRunnable() {
        updateSeekbar = object : Runnable {
            override fun run() {
                player_seekbar.progress = mediaPlayer.currentPosition
                seekbarHandler.postDelayed(this, 0)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isPlaying) {
            stopAudio()
        }
    }
}