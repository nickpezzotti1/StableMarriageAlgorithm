# StableMarriageAlgorithm
 This API was built in the context of the SEG major group project. Though it is built with encapsulation in mind. It can be used as a standalone version of the [Resident matching problem](https://en.wikipedia.org/wiki/National_Resident_Matching_Program#Matching_algorithm), a variant of the stable marriage algorithm that allows, in the classical interpreattion of the problem, a man to marry multiple women. In our case we allow a mentor to mentor multiple mentees.
 
 It allocates the pairs based on some predefined metrics. These can be edited in the individual user's class (Mentee > getScore() & Mentor > getScore()), though the weights given to the methods can easily be configured from within the request.
 
 This API is built to be easily deployable to AWS lambda, a serverless architecture, that once exposed to the outside world via an AWS API Gateway or to your VPC via an SNS.
