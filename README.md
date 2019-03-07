# StableMarriageAlgorithm
 This API was built in the context of the SEG major group project. Though it is built with encapsulation in mind. It can be used as a standalone version of the [Resident matching problem](https://en.wikipedia.org/wiki/National_Resident_Matching_Program#Matching_algorithm), a variant of the stable marriage algorithm that allows, in the classical interpreattion of the problem, a man to marry multiple women. In our case we allow a mentor to mentor multiple mentees.
 
 It allocates the pairs based on some predefined metrics. These can be edited in the individual user's class (Mentee > getScore() & Mentor > getScore()), though the weights given to the methods can easily be configured from within the request.
 
 This API is built to be easily deployable to AWS lambda, a serverless architecture, that once exposed to the outside world via an AWS API Gateway or to your VPC via an SNS.
 
 #endpoints
 
 ## input format
 format: json
 ```
 {
  "configurations": {
    "gender_importance": 100,
    "age_importance": 1,
    "hobbies_importance": 10,
    "interests_importance": 10
  },
  "mentors": [
    {
      "ID": "k173131",
      "partner_limit": 10,
      "age": 18,
      "gender": "male",
      "interests": ["AI", "Blockchain"],
      "hobbies": ["AI", "Blockchain"]
    }
  ],
  "mentees": [
    {
      "ID": "k1234121",
      "age": 18,
      "gender": "male",
      "interests": ["AI", "Blockchain"],
      "hobbies": ["AI", "Blockchain"]
    }
  ]
}
```
Notes:
```mentor.ID``` and ```mentee.ID``` are the only **mandatory** elements.

The ```configurations``` field is optional and if unset, or any of it's elements are not specified, they are defaulted to 1, 10, 5, 5.

## output format
``` { "assignments": [ { "mentee_id": "k1234121", "mentor_id": "k173131"}]} ``` 

**Note** Due to how AWS Lambda works as of 21/02/2019 the response will include ```\"``` instead of ```"``` because it escapes java quotes. We are working on a solution to this.
