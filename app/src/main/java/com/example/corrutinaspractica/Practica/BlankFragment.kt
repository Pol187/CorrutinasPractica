package com.example.corrutinaspractica.Practica

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.system.Os
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.corrutinaspractica.databinding.BlankFragmentBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class BlankFragment : Fragment() {


    private  lateinit var binding: BlankFragmentBinding

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 // ms
    private lateinit var job: CompletableJob
    private lateinit var job2:CompletableJob
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = BlankFragmentBinding.inflate(inflater)

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Barra1
        binding.jobButton.setOnClickListener(){
            if(!::job.isInitialized){
                iniciar()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }
        binding.ResetButton.setOnClickListener{
            if(::job.isInitialized){
                resetJob()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }
        binding.cancelButton.setOnClickListener(){
            parar()
        }

        //BArra2
        binding.jobButton2.setOnClickListener{
            if(!::job2.isInitialized){
                iniciar2()
            }
            binding.jobProgressBar2.startJobOrCancel(job2)
        }
        binding.ResetButton2.setOnClickListener{
            if(::job2.isInitialized){
                resetJob2()
            }
            binding.jobProgressBar2.startJobOrCancel(job)
        }
        binding.cancelButton2.setOnClickListener(){
            parar2()
        }
    }

    private fun parar() {
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting job"))
            binding.jobProgressBar.progress = 0

        }
    }
    private fun parar2() {
        if(job2.isActive || job2.isCompleted){
            job2.cancel(CancellationException("Resetting job"))
            binding.jobProgressBar2.progress = 0

        }
    }
    fun resetJob(){
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting job"))
        }
        iniciar()
    }
    fun resetJob2(){
        if(job2.isActive || job2.isCompleted){
            job2.cancel(CancellationException("Resetting job"))
        }
        iniciar2()
    }
    fun iniciar(){
        job = Job()
        job.invokeOnCompletion {
            it?.message.let{
                var msg = it
                if(msg.isNullOrBlank()){
                    msg = "Unknown cancellation error."
                }
                showToast(msg)
            }
        }
        binding.jobProgressBar.max = PROGRESS_MAX
        binding.jobProgressBar.progress = PROGRESS_START
    }
    fun iniciar2(){
        job2 = Job()
        job2.invokeOnCompletion {
            it?.message.let{
                var msg = it
                if(msg.isNullOrBlank()){
                    msg = "Unknown cancellation error."
                }
                showToast(msg)
            }
        }
        binding.jobProgressBar2.max = PROGRESS_MAX
        binding.jobProgressBar2.progress = PROGRESS_START
    }


    fun ProgressBar.startJobOrCancel(job: Job){
        if(this.progress > 0){
            resetJob()
        }
        else{
            CoroutineScope(IO + job).launch{
                for(i in PROGRESS_START..PROGRESS_MAX){
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
            }
        }
    }



    private fun showToast(text: String){
        GlobalScope.launch (Main){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}