package com.example.douyinclone.ui.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.douyinclone.BuildConfig
import com.example.douyinclone.R
import com.example.douyinclone.databinding.FragmentAiChatBinding
import com.example.douyinclone.adapter.AiChatAdapter
import com.example.douyinclone.data.model.ChatMessage
import com.example.douyinclone.data.remote.ChatMessageReq
import com.example.douyinclone.data.remote.ChatRequest
import com.example.douyinclone.data.remote.DoubaoApiClient
import kotlinx.coroutines.launch

class AiChatDialogFragment : DialogFragment() {
    
    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var chatAdapter: AiChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private val doubaoApi by lazy { DoubaoApiClient.create(BuildConfig.DOUBAO_API_KEY) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DouyinClone_Dialog)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupInputField()
        setupCloseButton()
        
        // 添加欢迎消息
        addMessage(ChatMessage(
            id = "welcome",
            content = "你好！我是AI助手，有什么可以帮助你的吗？",
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        ))
    }
    
    private fun setupRecyclerView() {
        chatAdapter = AiChatAdapter()
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }
    
    private fun setupInputField() {
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
        
        binding.ivSend.setOnClickListener {
            sendMessage()
        }
    }
    
    private fun setupCloseButton() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }
    
    private fun sendMessage() {
        val content = binding.etMessage.text.toString().trim()
        if (content.isEmpty()) return
        
        // 添加用户消息
        addMessage(ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            content = content,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        ))
        
        binding.etMessage.text.clear()
        requestAiResponse(content)
    }
    
    private fun requestAiResponse(userMessage: String) {
        val apiKey = BuildConfig.DOUBAO_API_KEY
        val fallback = "AI 暂时不可用，请稍后再试。"
        
        android.util.Log.d("AiChat", "API Key length: ${apiKey.length}")
        android.util.Log.d("AiChat", "API Key: ${apiKey.take(10)}...")
        
        if (apiKey.isBlank()) {
            android.util.Log.e("AiChat", "API Key is blank!")
            addMessage(ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                content = fallback,
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            ))
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                android.util.Log.d("AiChat", "Sending request to API...")
                android.util.Log.d("AiChat", "Using model: ${BuildConfig.DOUBAO_MODEL}")
                val response = doubaoApi.chat(
                    ChatRequest(
                        model = BuildConfig.DOUBAO_MODEL,
                        messages = listOf(ChatMessageReq(role = "user", content = userMessage))
                    )
                )
                
                android.util.Log.d("AiChat", "Response received: ${response.choices.size} choices")
                
                val reply = response.choices.firstOrNull()
                    ?.message
                    ?.content
                    ?.takeIf { it.isNotBlank() }
                    ?: fallback
                
                addMessage(ChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    content = reply,
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                ))
            } catch (e: Exception) {
                android.util.Log.e("AiChat", "Error calling API: ${e.message}", e)
                addMessage(ChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    content = "$fallback\n错误: ${e.message}",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                ))
            }
        }
    }
    
    private fun addMessage(message: ChatMessage) {
        messages.add(message)
        chatAdapter.submitList(messages.toList())
        binding.rvChat.scrollToPosition(messages.size - 1)
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        const val TAG = "AiChatDialogFragment"
        
        fun newInstance(): AiChatDialogFragment {
            return AiChatDialogFragment()
        }
    }
}
