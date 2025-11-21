package com.example.douyinclone.ui.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.douyinclone.R
import com.example.douyinclone.databinding.FragmentAiChatBinding
import com.example.douyinclone.adapter.AiChatAdapter
import com.example.douyinclone.data.model.ChatMessage

class AiChatDialogFragment : DialogFragment() {
    
    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var chatAdapter: AiChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    
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
        
        // 模拟AI回复
        binding.root.postDelayed({
            generateAiResponse(content)
        }, 1000)
    }
    
    private fun generateAiResponse(userMessage: String) {
        // 简单的模拟AI回复
        val response = when {
            userMessage.contains("你好") || userMessage.contains("hi", ignoreCase = true) -> 
                "你好！很高兴见到你！有什么我可以帮助你的吗？"
            userMessage.contains("视频") -> 
                "这个应用是一个短视频平台，你可以在这里观看各种有趣的视频内容！"
            userMessage.contains("功能") -> 
                "这个应用支持双列瀑布流浏览、视频播放、点赞、评论等功能。"
            userMessage.contains("帮助") || userMessage.contains("help", ignoreCase = true) -> 
                "我可以回答你关于这个应用的问题，或者和你聊天。试着问我一些问题吧！"
            else -> 
                "收到你的消息了！这是一个演示版本的AI助手，未来会支持更多智能对话功能。"
        }
        
        addMessage(ChatMessage(
            id = "ai_${System.currentTimeMillis()}",
            content = response,
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        ))
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
