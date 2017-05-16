class AuthenticationController < ApplicationController
  skip_before_action :authenticate_request

  def authenticate
    command = AuthenticateUser.call(params[:email], params[:password])

    if command.success?
      render json: {auth_token: command.result}
    else
      render json: {error: command.errors}, status: :unauthorized
    end
  end

  def register
    @user = User.create(nick: params[:nick], email: params[:email], password: params[:password])
    if @user.save
      render json: {user: @user}, status: :ok;
    else
      render json: {error: @user.errors}, status: :not_acceptable;
    end
  end
end
