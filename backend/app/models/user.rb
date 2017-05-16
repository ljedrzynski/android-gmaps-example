class User < ApplicationRecord
  validates :nick, :email, uniqueness: true
  validates :nick, :email, :password_digest, presence: true
  has_secure_password
end
