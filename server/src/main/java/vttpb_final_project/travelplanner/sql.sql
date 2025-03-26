drop database if exists railway;

create database railway;
use railway;

CREATE TABLE users (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) unique NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_preferences (
    username varchar(255) PRIMARY KEY,
    diet VARCHAR(50) default '',
    allergies varchar(255) default '',
    serving int default 2,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

create table meals (
	id int auto_increment primary key,
    username varchar(255) not null,
    mealDate date not null,
    mealTime ENUM('Morning', 'Afternoon', 'Evening') not null,
    mealId int not null,
    mealName varchar(255) not null,
    unique(username, mealDate, mealTime),
    foreign key (username) references users (username) ON DELETE CASCADE
);

create table recipe_ingredients (
    recipe_id int,  
    username varchar(255) not null,
    ingredientName varchar(255) not null, 
    quantity decimal(10,2) not null, 
    unit varchar(10),
    foreign key (username) references users(username)
);

create table grocery_list (
	ingredient_name varchar(255) not null primary key,
    quantity decimal(10,2),
    unit varchar(10),  
    username varchar(255),
    status ENUM('Pending', 'Cooked', 'Bought') default 'Pending',
    foreign key (username) references users(username)
);

create table products (
	id int primary key,
    name varchar(255) not null,
    price decimal(4,2) not null,
    image varchar(255) not null,
    category varchar(64) not null,
    description text not null
);

insert into products values(1, 'Chicken Breast', 3.00, 'https://media.nedigital.sg/fairprice/90199913_XL1_20250325124921_bf72fd5cd934ca82d0b5647ea05b85a9.jpg?w=320&q=60', 'Meat', '500g (Boneless, skinless)');
insert into products values(2, 'Frozen Fish', 14.00, 'https://media.nedigital.sg/fairprice/90007411_XL1_20250325123351_ad831b97cd6d760b0711a342078e93b6.jpg?w=320&q=60', 'Meat', '700g (Frozen fillets)');
insert into products values(3, 'Broccoli', 3.00, 'https://media.nedigital.sg/fairprice/90144331_XL1_20250325125719_e82e0f3dbd219aa9620bb93cc113eb62.jpg?w=320&q=60', 'Vegetables', '300g (Fresh florets)');
insert into products values(4, 'Milk', 7.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/10238055_XL1_20240506.jpg?w=320&q=60', 'Dairy', '1L (Full-cream milk)');
insert into products values(5, 'Eggs', 4.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/283672_XL1_20240718.jpg?w=320&q=60', 'Dairy', '600g (10 per pack)');
insert into products values(6, 'Salt', 1.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/432823_XL1_20210504.jpg?w=320&q=60', 'Seasonings', '500g (Refined)');
insert into products values(7, 'Olive Oil', 12.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/12202849_XL1_20250127.jpg?w=320&q=60', 'Seasonings', '500ml (Extra virgin)');
insert into products values(8, 'Beef minced', 8.00, 'https://media.nedigital.sg/fairprice/90046419_XL1_20250324145728_636e78e1471d8ab6747d20bd69ad3aca.jpg?w=320&q=60', 'Meat', '400g (Lean minced beef)');
insert into products values(9, 'Blueberries', 4.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/10632060_XL1_20250224.jpg?w=320&q=60', 'Fruits', '125g (Fresh)');
insert into products values(10, 'Carrot', 3.00, 'https://media.nedigital.sg/fairprice/90152091_XL1_20250313143241_3fd503b1b4640426ad59b97c4b1ca365.jpg?w=320&q=60', 'Vegetables', '500g (Fresh)');
insert into products values(11, 'Pepper', 4.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/11046864_XL1_20210205.jpg?w=320&q=60', 'Seasonings', '100g (Ground)');
insert into products values(12, 'Sugar', 2.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/13179115_XL1_20210824.jpg?w=320&q=60', 'Baking', '500g (Granulated)');
insert into products values(13, 'Flour', 2.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/13185112_XL1_20210121.jpg?w=320&q=60', 'Baking', '1kg (All-purpose)');
insert into products values(14, 'Onion', 2.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/10736396_XL1_20230525.jpg?w=320&q=60', 'Vegetables', '500g (Fresh)');
insert into products values(15, 'Garlic', 3.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/10734550_XL1_20221122.jpg?w=320&q=60', 'Vegetables', '200g (Fresh cloves)');
insert into products values(16, 'Mushroom', 4.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/13101275_XL1.jpg?w=320&q=60', 'Vegetables', '200g (Fresh)');
insert into products values(17, 'Potato', 2.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/13216391_XL1_20221122.jpg?w=320&q=60', 'Vegetables', '1kg (Fresh)');
insert into products values(18, 'Parsley', 4.00, 'https://media.nedigital.sg/fairprice/90175105_XL1_20250325120610_c55e4635ac5db81a5e3a5ecc98078d0a.jpg?w=320&q=60', 'Vegetables', '100g (Fresh)');
insert into products values(19, 'Tomato', 2.00, 'https://media.nedigital.sg/fairprice/fpol/media/images/product/XL/12228635_XL1_20221123.jpg?w=320&q=60', 'Vegetables', '500g (Fresh)');
insert into products values(20, 'Banana', 4.00, 'https://media.nedigital.sg/fairprice/90111866_XL1_20250325150736_69a9454f5944d8dffc94e6d030b8095a.jpg?w=320&q=60', 'Fruits', '600g (Fresh)');

create table orders (
	id varchar(10) not null primary key,
	stripeSessionId varchar(255),
    customerEmail varchar(255) not null,
    orderDate date not null,
    status ENUM('Pending', 'Paid', 'Failed') default 'Pending',
    totalAmount bigint not null,
    currency varchar(10) not null
);

create table order_items (
	id bigint not null auto_increment primary key,
    order_id varchar(10) not null,
    productName varchar(255),
    price bigint not null,
    quantity int not null,
    foreign key (order_id) references orders (id) on delete cascade,
    index (order_id)
);