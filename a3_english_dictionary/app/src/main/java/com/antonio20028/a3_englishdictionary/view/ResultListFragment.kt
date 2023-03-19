package com.antonio20028.a3_englishdictionary.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.antonio20028.a3_englishdictionary.R
import com.antonio20028.a3_englishdictionary.model.data.WordModel
import com.antonio20028.a3_englishdictionary.model.handlers.AudioDownloaderService
import com.antonio20028.a3_englishdictionary.model.repository.GarbageWordsList
import com.antonio20028.a3_englishdictionary.model.repository.WordsRepository
import com.antonio20028.a3_englishdictionary.view.adapters.WordMeaningItemAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.Text
import java.net.URL

class ResultListFragment : Fragment() {


    private lateinit var meaningRecyclerView: RecyclerView

    private val json = Json { ignoreUnknownKeys = true }
    lateinit var textWord: TextView
    lateinit var btnAudioPlayer: ImageView
    lateinit var txtPhonetic:TextView

    var heading:String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.result_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meaningRecyclerView = view.findViewById(R.id.word_meaning_recycler_view)
        textWord = view.findViewById(R.id.txt_word)
        btnAudioPlayer = view.findViewById(R.id.btn_audio_player)
        txtPhonetic = view.findViewById(R.id.txt_phonetic)
        meaningRecyclerView.setHasFixedSize(true)

        val args = arguments
        var data: String? = null
        if (args != null) {
            data = args.getString("JsonData")
        }

        val ser = data?.let { json.decodeFromString<List<WordModel>>(it) }
        val word = ser?.first()
        heading = word?.word.toString()
        textWord.text =heading
        val meanings = word?.meanings

        val phonetic  = requireContext().resources.getString(R.string.phonetic, word?.phonetic ?: "")
        txtPhonetic.text = phonetic
        meaningRecyclerView.adapter =
            meanings?.let { WordMeaningItemAdapter(requireContext(), meanings = it) }

        btnAudioPlayer.setOnClickListener(View.OnClickListener {
            if ((word?.phonetics != null) && word.phonetics.isNotEmpty()) {

                val audioUrlString = word.phonetics.first { phonetic-> !phonetic.audio.isNullOrEmpty()}

                if (audioUrlString.audio.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No Audio", Toast.LENGTH_LONG).show()
                } else {
                    val url: URL = URL(audioUrlString.audio)
                    val audioDownloadTask = AudioDownloaderService(callback = {}, context = requireContext())

                    audioDownloadTask.execute(url)
                }
            } else {
                Toast.makeText(requireContext(), "No audio found", Toast.LENGTH_LONG).show()
            }
        })
    }

}