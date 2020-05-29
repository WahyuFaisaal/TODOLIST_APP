package id.ac.unhas.wahyufaisal.todolist_app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.SearchManager
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.ac.unhas.wahyufaisal.todolist_app.db.Todo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_todo.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        var isSortByDateCreated = true
    }

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var alarmReminder: Reminder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)

        todoRV.layoutManager = layoutManager
        todoAdapter = TodoAdapter(){ todo, _ ->
            val options = resources.getStringArray(R.array.option_edit_delete)
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(title)
            alertBuilder.setItems(options) { dialog, i ->
                when (i) {
                    0 -> showDetailsDialog(todo)
                    1 -> showEditDialog(todo)
                    2 -> showDeleteDialog(todo)
                }
            }.show()
        }

        todoRV.adapter = todoAdapter
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        fab_btn.setOnClickListener {
            showInsertDialog()
        }

        alarmReminder = Reminder()
    }

    override fun onResume() {
        super.onResume()
        observeData()
    }

    private fun observeData(){
        todoViewModel.getTodos()?.observe(this, Observer {
            todoAdapter.setTodoList(it)
        })
    }

    private fun showInsertDialog(){
        val view = LayoutInflater.from(this).inflate(R.layout.fragment_todo, null)

        view.input_date.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    val myFormat = "dd/MM/yy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    view.input_date.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        view.input_due.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    val myFormat = "HH:mm"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    view.input_due.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
                .show()
        }

        val dialogTitle = "Add data"
        val toastMessage = "Data has been added successfully"
        val failAlertMessage = "Please fill all the required fields"

        FormDialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val date = view.input_date.text.toString().trim()
            val time = view.input_due.text.toString().trim()
            val note = view.input_note.text.toString()

            val remindMe = view.input_remindme.isChecked

            if (title == "" || date == "" || time == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parsedDate = SimpleDateFormat("dd/MM/yy", Locale.US).parse(date) as Date
                val dueDate = parsedDate.toString("dd/MM/yy")

                val currentDate = Calendar.getInstance().time
                val dateCreated = currentDate.toString("dd/MM/yy HH:mm:ss")

                val todo = Todo(
                    title = title,
                    note = note,
                    dateCreated = dateCreated,
                    dateUpdated = dateCreated,
                    dueDate = dueDate,
                    dueTime = time,
                    remindMe = remindMe
                )

                todoViewModel.insertTodo(todo)

                if (remindMe) {
                    alarmReminder.setReminderAlarm(this, dueDate, time,"$title is due in 1 hour")
                }
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDetailsDialog(todo: Todo) {
        val title = "Title: ${todo.title}"
        val dueDate = "Due date : ${todo.dueDate}, ${todo.dueTime}"
        val note = "Note: ${todo.note}"
        val dateCreated = "Date created: ${todo.dateCreated}"
        val dateUpdated = "Date updated: ${todo.dateUpdated}"

        val strReminder = if(todo.remindMe) "Enabled" else "Disabled"
        val remindMe = "Reminder: $strReminder"

        val strMessage = "$title\n$dueDate\n$note\n\n$dateCreated\n$dateUpdated\n$remindMe"

        AlertDialog.Builder(this).setMessage(strMessage).setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

    private fun showEditDialog(todo: Todo) {
        val view = LayoutInflater.from(this).inflate(R.layout.fragment_todo, null)

        view.input_date.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    val myFormat = "dd/MM/yy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    view.input_date.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        view.input_due.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    val myFormat = "HH:mm"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    view.input_due.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
                .show()
        }

        view.input_title.setText(todo.title)
        view.input_note.setText(todo.note)
        view.input_date.setText(todo.dueDate)
        view.input_due.setText(todo.dueTime)
        view.input_remindme.isChecked = todo.remindMe

        val dialogTitle = "Edit data"
        val toastMessage = "Data has been updated"
        val failAlertMessage = "Please fill all the required fields"

        FormDialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val date = view.input_date.text.toString().trim()
            val time = view.input_due.text.toString().trim()
            val note = view.input_note.text.toString()

            val remindMe = view.input_remindme.isChecked
            val dateCreated = todo.dateCreated
            val prevDueTime = todo.dueTime

            if (title == "" || date == "" || time == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parsedDate = SimpleDateFormat("dd/MM/yy", Locale.US).parse(date) as Date
                val dueDate = parsedDate.toString("dd/MM/yy")

                val currentDate = Calendar.getInstance().time
                val dateUpdated = currentDate.toString("dd/MM/yy HH:mm:ss")

                todo.title = title
                todo.note = note
                todo.dateCreated = dateCreated
                todo.dateUpdated = dateUpdated
                todo.dueDate = dueDate
                todo.dueTime = time
                todo.remindMe = remindMe

                todoViewModel.updateTodo(todo)

                if (remindMe && prevDueTime!=time) {
                    alarmReminder.setReminderAlarm(this, dueDate, time,"$title is due in 1 hour")
                }
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDeleteDialog(todo: Todo) {
        val toastMessage = "Data has been deleted"

        todoViewModel.deleteTodo(todo)
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = (menu.findItem(R.id.search)).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search tasks"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                todoAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoAdapter.filter.filter(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort -> true
            R.id.sort_by_create -> {
                isSortByDateCreated = true
                observeData()
                true
            }
            R.id.sort_by_due -> {
                isSortByDateCreated = false
                observeData()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}