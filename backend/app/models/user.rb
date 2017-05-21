class User < ApplicationRecord

  has_many :created_park_spaces, class_name: 'ParkingSpace', foreign_key: :reporter_user_id
  has_one :curr_park_space, class_name: 'ParkingSpace', foreign_key: :curr_occupier_user_id
  has_one :last_park_space, class_name: 'ParkingSpace', foreign_key: :last_occupier_user_id

  validates :nick, :email, uniqueness: true
  validates :nick, :email, :password_digest, presence: true
  has_secure_password
end
