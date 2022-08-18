package com.example.pokemon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_pokemon.view.*

class PokeListAdapter(context: Context, pokemon: List<Pokemon>): ArrayAdapter<Pokemon>(context, 0, pokemon) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val poke = getItem(position)
        val holder: ViewHolder
        val view: View

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        poke?.let {
            Glide.with(context).load(poke.coverUrl).into(holder.imgPoke)
            holder.txtNome.text = poke.name
        }
        return view
    }

    internal class ViewHolder(view: View){
        var imgPoke: ImageView = view.imgPoke
        var txtNome: TextView = view.txtTitulo
    }
}