package id.ac.unhas.wahyufaisal.todolist_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import id.ac.unhas.wahyufaisal.todolist_app.db.Todo
import id.ac.unhas.wahyufaisal.todolist_app.db.TodoRepository

class TodoViewModel(application: Application) : AndroidViewModel(application){

    private var todoRepository = TodoRepository(application)
    private var todos: LiveData<List<Todo>>? = todoRepository.getTodos()

    fun insertTodo(todo: Todo) {
        todoRepository.insert(todo)
    }

    fun  getTodos(): LiveData<List<Todo>>? {
        return todos
    }

    fun deleteTodo(todo: Todo) {
        todoRepository.delete(todo)
    }

    fun updateTodo(todo: Todo) {
        todoRepository.update(todo)
    }
}