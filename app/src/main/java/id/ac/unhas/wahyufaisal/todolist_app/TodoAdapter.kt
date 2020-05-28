package id.ac.unhas.wahyufaisal.todolist_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.unhas.wahyufaisal.todolist_app.db.Todo
import kotlinx.android.synthetic.main.fragment_todo.view.*
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.recyclerview_item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(private val listener: (Todo, Int) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_TODO = 1

    private var todoList = listOf<Todo>()

    fun setTodoList(todoList: List<Todo>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (todoList.isEmpty())
            VIEW_TYPE_EMPTY
        else
            VIEW_TYPE_TODO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            VIEW_TYPE_TODO -> TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_todo, parent, false))
            VIEW_TYPE_EMPTY -> EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_empty, parent, false))
            else -> throw throw IllegalArgumentException("Undefined view type")
        }
    }

    override fun getItemCount(): Int = if (todoList.isEmpty()) 1 else todoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_EMPTY -> {
                val emptyHolder = holder as EmptyViewHolder
                emptyHolder.bindItem()
            }
            VIEW_TYPE_TODO -> {
                val todoHolder = holder as TodoViewHolder

                todoHolder.bindItem(todoList[position], listener)
            }
        }
    }

    class TodoViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(todo: Todo, listener: (Todo, Int) -> Unit) {
            val parsedDateCreated = SimpleDateFormat("dd/MM/yy", Locale.US).parse(todo.dateCreated) as Date
            val dateCreated = parsedDateCreated.toString("dd MMM yyyy")

            val parsedDateUpdated = SimpleDateFormat("dd/MM/yy", Locale.US).parse(todo.dateCreated) as Date
            val dateUpdated = parsedDateUpdated.toString("dd MMM yyyy")

            val date = if (todo.dateUpdated != todo.dateCreated) "Updated at $dateUpdated" else "Created at $dateCreated"

            val parsedDate = SimpleDateFormat("dd/MM/yy", Locale.US).parse(todo.dueDate) as Date
            val dueDate = parsedDate.toString("dd MMM yyyy")

            val dueDateTime = "Due ${dueDate}, ${todo.dueTime}"

            itemView.title_todo.text = todo.title
            itemView.note_todo.text = todo.note
            itemView.due_date.text = dueDateTime
            itemView.date_create.text = date

            itemView.setOnClickListener{
                listener(todo, layoutPosition)
            }
        }

        fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }
    }

    class EmptyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(){
            itemView.empty_tv.text = "No data found"
        }
    }
}