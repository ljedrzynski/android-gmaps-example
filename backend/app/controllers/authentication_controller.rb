class AuthenticationController < ApplicationController
  skip_before_action :authenticate_request

  def authenticate
    @user = authenticate_user(params[:email], params[:password])

    unless @user.present?
      render json: {:user_authentication => 'invalid credentials'}, status: :unauthorized and return
    end

    command = TokenProvider.call(@user)

    if command.success?
      render json: {id: @user.id, nick: @user.nick, email: @user.email, auth_token: command.result}, status: :ok
    else
      render json: {error: command.errors}, status: :internal_server_error
    end

  end

  def register
    @user = User.create(nick: params[:nick], email: params[:email], password: params[:password])
    if @user.save
      render json: {user_id: @user.id}, status: :ok;
    else
      render json: {error: @user.errors}, status: :not_acceptable;
    end
  end

  def authenticate_user(email, password)
    user = User.find_by_email(email)
    return user if user && user.authenticate(password)
    nil
  end
end
