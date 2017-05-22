Rails.application.routes.draw do

  namespace :api, defaults: {format: :json} do
    namespace :v1 do
      resources :parking_spaces, :path => 'parking-spaces'
    end
  end
  
  post 'authenticate', to: 'authentication#authenticate'
  post 'register', to: 'authentication#register'
end