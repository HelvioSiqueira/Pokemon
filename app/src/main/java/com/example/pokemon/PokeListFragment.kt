package com.example.pokemon

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.pokemon_list.*

class PokeListFragment : Fragment() {

    private var asyncTask: PokeDownloadTask? = null
    private val pokeList = mutableListOf<Pokemon>()
    private var adapter: ArrayAdapter<Pokemon>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
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
            if (asyncTask == null){
                if(PokeHttp.hasConnection(requireContext())){
                    startDownloadJson()
                } else {
                    progressBar.visibility = View.GONE
                    txtMessage.setText(R.string.error_no_connection)
                }
            } else if(asyncTask?.status == AsyncTask.Status.RUNNING){
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
        if(asyncTask?.status != AsyncTask.Status.RUNNING){
            asyncTask = PokeDownloadTask()
            asyncTask?.execute()
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
        asyncTask = null
    }

    inner class PokeDownloadTask: AsyncTask<Void, Void, List<Pokemon>?>(){
        override fun onPreExecute() {
            super.onPreExecute()
            showProgress(true)
        }

        override fun doInBackground(vararg string: Void?): List<Pokemon>? {
            return PokeHttp.loadPokemon()
        }

        override fun onPostExecute(pokemon: List<Pokemon>?) {
            super.onPostExecute(pokemon)
            showProgress(false)
            updatePokeList(pokemon)
        }
    }
}