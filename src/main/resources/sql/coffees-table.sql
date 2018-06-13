CREATE TABLE IF NOT EXISTS `coffees` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` enum('flatwhite','latte','macchiato','cappuccino','longblack','piccolo') NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` datetime DEFAULT NULL,
  `orderId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;