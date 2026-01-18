//chat_messagesテーブルへのCRUD操作
package com.basemarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.ChatMessages;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;

@Repository
public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Long> {

	// 商品ごとのチャットメッセージ一覧
	List<ChatMessages> findByItemOrderByCreatedAtAsc(Items item);

	// 送信者 or 受信者ごとのメッセージ一覧
	List<ChatMessages> findBySenderOrReceiverOrderByCreatedAtAsc(Users sender, Users receiver);
}
