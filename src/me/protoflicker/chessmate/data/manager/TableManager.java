package me.protoflicker.chessmate.data.manager;

import com.google.common.cache.*;
import me.protoflicker.chessmate.data.Database;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public abstract class TableManager<T> implements TableManagerInterface<T> {

	protected final Database database;

	protected final LoadingCache<String, T> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(30))
			.removalListener((RemovalListener<String, T>) this::onRemoval)
			.build(new CacheLoader<String, T>() {
				@Override
				public T load(String id) {
					return loadRecord(id);
				}
			});

	public TableManager(Database database){
		this.database = database;
	}

	@Override
	public T getRecord(String id){
		try {
			return cache.get(id);
		} catch (ExecutionException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addRecord(T record){
		cache.put(getId(record), record);
		insertRecord(record);
	}

	private void onRemoval(RemovalNotification<String, T> notification){
		if(notification.getValue() != null){
			updateRecord(notification.getValue());
		}
	}
}
