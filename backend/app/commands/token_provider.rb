class TokenProvider
  prepend SimpleCommand

  def initialize(user)
    @user = user
  end

  def call
    JsonWebToken::encode(user_id: @user.id) if @user
  end
end