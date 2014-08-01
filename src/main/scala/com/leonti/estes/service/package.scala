package com.leonti.estes

import com.leonti.estes.dao.impl.IngredientDaoMongo
import com.leonti.estes.dao.impl.DishDaoMongo
import com.leonti.estes.dao.impl.OrderDaoMongo
import com.leonti.estes.dao.impl.WaiterDaoMongo

package object service {
	
	object DishService extends DishDaoMongo
	object IngredientService extends IngredientDaoMongo 
	object OrderService extends OrderDaoMongo
	object WaiterService extends WaiterDaoMongo
	
}