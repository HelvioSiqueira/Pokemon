package com.example.pokemon

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.pokemon_list.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PokeListFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    private var downloadJob: Job? = null

    private val pokeList = mutableListOf<Pokemon>()
    private var adapter: ArrayAdapter<Pokemon>? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pokemon_list, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = PokeListAdapter(requireContext(), pokeList)
        listView.emptyView = txtMessage
        listView.adapter = adapter

        if (pokeList.isNotEmpty()){
            showProgress(false)
        } else {
            if (downloadJob == null){
                if(PokeHttp.hasConnection(requireContext())){
                    startDownloadJson()
                } else {
                    progressBar.visibility = View.GONE
                    txtMessage.setText(R.string.error_no_connection)
                }
            } else if(downloadJob?.isActive == true){
                showProgress(true)
            }
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            txtMessage.setText(R.string.message_progress)
        }

        txtMessage.visibility = if (show) View.VISIBLE else View.GONE
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun startDownloadJson(){
        downloadJob = launch {
            showProgress(true)

            val pokeTask = withContext(Dispatchers.IO){
                PokeHttp.loadPokemon()
            }

            updatePokeList(pokeTask)
            showProgress(false)
        }
    }

    private fun updatePokeList(result: List<Pokemon>?){
        if (result != null){
            pokeList.clear()
            pokeList.addAll(result)
        } else {
            txtMessage.setText(R.string.error_load_books)
        }

        adapter?.notifyDataSetChanged()
        downloadJob = null
    }
}