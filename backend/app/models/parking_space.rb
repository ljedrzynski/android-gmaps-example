class ParkingSpace < ApplicationRecord
  reverse_geocoded_by :latitude, :longitude
  belongs_to :curr_occupier, class_name: 'User', optional: true
  belongs_to :last_occupier, class_name: 'User', optional: true
  belongs_to :reporter, class_name: 'User', optional: true
end
